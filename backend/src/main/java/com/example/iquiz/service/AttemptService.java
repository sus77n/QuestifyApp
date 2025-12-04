package com.example.iquiz.service;

import com.example.iquiz.dto.attempt.AttemptResponseDto;
import com.example.iquiz.dto.attempt.AttemptStartResponseDto;
import com.example.iquiz.dto.attemptDetail.ResultDto;
import com.example.iquiz.dto.exercise.ExerciseResponseDto;
import com.example.iquiz.dto.attemptDetail.AttemptDetailDto;
import com.example.iquiz.entity.*;
import com.example.iquiz.enums.AttemptStatus;
import com.example.iquiz.exception.ConflictException;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.mapper.ExerciseMapper;
import com.example.iquiz.repository.*;

import com.example.iquiz.utility.SubmissionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttemptService {

    private final AttemptRepository attemptRepository;
    private final ExerciseRepository exerciseRepository;
    private final AttemptDetailRepository attemptDetailRepository;
    private final UserRepository userRepository;
    private final LearningUnitRepository learningUnitRepository;
    private final LessonConfigRepository lessonConfigRepository;
    private final SubmissionUtil submissionUtil;
    private final ExerciseCategoryDistributionRepository exerciseCategoryDistributionRepository;
    private final ExerciseMapper exerciseMapper;
    private final UserMasteryRepository userMasteryRepository;


    public Attempt save(Attempt attempt) {
        return attemptRepository.save(attempt);
    }

    public Attempt findById(UUID id) {
        return attemptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt", "Id", id));
    }

    public List<Attempt> findByUser(UUID userId) {
        return attemptRepository.findByUserId(userId);
    }

    public List<Attempt> findByLesson(UUID lessonId) {
        return attemptRepository.findByLessonId(lessonId);
    }

    public void delete(UUID id) {
        attemptRepository.deleteById(id);
    }

    @Transactional
    public AttemptStartResponseDto startAttempt(UUID userId, UUID lessonId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

        LearningUnit lesson = learningUnitRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("LearningUnit", "Id", lessonId));

        int attemptNo = attemptRepository.findByUserIdAndLessonId(userId, lessonId).size() + 1;

        Attempt attempt = new Attempt();
        attempt.setUser(user);
        attempt.setLesson(lesson);
        attempt.setAttemptNo(attemptNo);
        attempt.setAttemptStatus(AttemptStatus.IN_PROGRESS);
        attempt.setStartedAt(LocalDateTime.now());
        attemptRepository.save(attempt);

        LessonConfig config = lessonConfigRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson Config", "Id", lessonId));

        int totalQuestions = config.getQuestionsPerAttempt();
        if (totalQuestions <= 0) {
            return new AttemptStartResponseDto(
                    attempt.getId(),
                    userId,
                    lessonId,
                    attemptNo,
                    attempt.getStartedAt(),
                    List.of()
            );
        }

        Set<UUID> usedExerciseIds = Collections.emptySet();
        if (config.isNoRepeatScope()) {
            List<UUID> usedIds =
                    attemptDetailRepository.findUsedExerciseIdsByUserAndLesson(userId, lessonId);
            usedExerciseIds = new HashSet<>(usedIds);
        }
        final Set<UUID> finalUsedExerciseIds = usedExerciseIds;
        Random random = new Random();

        List<UUID> exCategoryIds = lesson.getChildren().stream()
                .map(LearningUnit::getId)
                .toList();

        List<Exercise> candidatePool = exerciseRepository.findByParent_IdIn(exCategoryIds);

        if (config.isNoRepeatScope()) {
            candidatePool.removeIf(ex -> finalUsedExerciseIds.contains(ex.getId()));
        }

        if (candidatePool.size() < totalQuestions && config.isNoRepeatScope()) {
            List<Exercise> allForLesson = exerciseRepository.findByParent_IdIn(exCategoryIds);

            Set<UUID> existingIds = candidatePool.stream()
                    .map(Exercise::getId)
                    .collect(Collectors.toSet());

            for (Exercise ex : allForLesson) {
                if (!existingIds.contains(ex.getId())) {
                    candidatePool.add(ex);
                    existingIds.add(ex.getId());
                }
            }
        }

        if (candidatePool.isEmpty()) {
            throw new ConflictException("No exercises available for this lesson");
        }

        Map<UUID, List<Exercise>> poolByCategory = candidatePool.stream()
                .collect(Collectors.groupingBy(ex -> ex.getParent().getId()));

        Set<UUID> categoryIds = poolByCategory.keySet();

        List<ExerciseCategoryDistribution> distributions =
                exerciseCategoryDistributionRepository.findByParentLessonId(lessonId);

        Map<UUID, ExerciseCategoryDistribution> distByCategory = distributions.stream()
                .collect(Collectors.toMap(
                        d -> d.getId().getExerciseCategoryId(),
                        Function.identity(),
                        (d1, d2) -> d1
                ));

        List<UserMastery> masteryList =
                userMasteryRepository.findByUserIdAndLessonId(userId, lessonId);

        Map<UUID, UserMastery> masteryByCategory = masteryList.stream()
                .collect(Collectors.toMap(
                        m -> m.getId().getExerciseCategoryId(),
                        Function.identity(),
                        (m1, m2) -> m1
                ));

        class CatInfo {
            UUID categoryId;
            double baseWeight;
            Double minPerAttempt;   // can be null
            Double maxPerAttempt;   // can be null
            double accuracy;
            double weight;          // (1 - accuracy) * baseWeight
            double rate;
            int targetCount;        // final number of questions to pick
            double fracPart;        // fractional part of raw allocation
        }

        List<CatInfo> catInfos = new ArrayList<>();

        for (UUID catId : categoryIds) {
            ExerciseCategoryDistribution dist = distByCategory.get(catId);
            double baseWeight = (dist != null && dist.getBaseWeight() != null)
                    ? dist.getBaseWeight().doubleValue()
                    : 1.0;

            Double minPerAttempt = (dist != null && dist.getMinPerAttempt() != null)
                    ? dist.getMinPerAttempt().doubleValue()
                    : null;

            Double maxPerAttempt = (dist != null && dist.getMaxPerAttempt() != null)
                    ? dist.getMaxPerAttempt().doubleValue()
                    : null;

            UserMastery mastery = masteryByCategory.get(catId);
            double accuracy = (mastery != null) ? mastery.getAccuracy() : 0.5; // default 50%

            CatInfo info = new CatInfo();
            info.categoryId = catId;
            info.baseWeight = baseWeight;
            info.minPerAttempt = minPerAttempt;
            info.maxPerAttempt = maxPerAttempt;
            info.accuracy = accuracy;
            info.weight = (1.0 - accuracy) * baseWeight;
            catInfos.add(info);
        }

        // 9. If all weights are 0 (user mastered everything), fall back to baseWeight
        double totalWeight = catInfos.stream().mapToDouble(ci -> ci.weight).sum();
        if (totalWeight <= 0) {
            catInfos.forEach(ci -> ci.weight = ci.baseWeight);
            totalWeight = catInfos.stream().mapToDouble(ci -> ci.weight).sum();
        }

        // 10. Compute rate & initial integer counts
        for (CatInfo ci : catInfos) {
            ci.rate = ci.weight / totalWeight;
            double raw = totalQuestions * ci.rate;
            int floor = (int) Math.floor(raw);
            ci.targetCount = floor;
            ci.fracPart = raw - floor;
        }

        // 11. Apply min/max and cap by available pool size
        for (CatInfo ci : catInfos) {
            if (ci.minPerAttempt != null) {
                ci.targetCount = Math.max(ci.targetCount, ci.minPerAttempt.intValue());
            }
            if (ci.maxPerAttempt != null) {
                ci.targetCount = Math.min(ci.targetCount, ci.maxPerAttempt.intValue());
            }

            int available = poolByCategory.get(ci.categoryId).size();
            if (ci.targetCount > available) {
                ci.targetCount = available;
            }
        }

        // 12. Rebalance so sum(targetCount) ~= totalQuestions
        int currentTotal = catInfos.stream().mapToInt(ci -> ci.targetCount).sum();

        // 12.a Add if we are short
        while (currentTotal < totalQuestions) {
            CatInfo best = catInfos.stream()
                    .filter(ci -> {
                        int available = poolByCategory.get(ci.categoryId).size();
                        return ((ci.maxPerAttempt == null || ci.targetCount < ci.maxPerAttempt)
                                && ci.targetCount < available);
                    })
                    .max(Comparator.comparingDouble(ci -> ci.fracPart))
                    .orElse(null);

            if (best == null) break;
            best.targetCount++;
            currentTotal++;
        }

        // 12.b Remove if we exceed
        while (currentTotal > totalQuestions) {
            CatInfo worst = catInfos.stream()
                    .filter(ci -> ci.minPerAttempt == null || ci.targetCount > ci.minPerAttempt)
                    .min(Comparator.comparingDouble(ci -> ci.fracPart))
                    .orElse(null);

            if (worst == null) break;
            worst.targetCount--;
            currentTotal--;
        }

        // 13. Pick exercises per category, according to targetCount
        List<Exercise> selectedExercises = new ArrayList<>();

        for (CatInfo ci : catInfos) {
            List<Exercise> pool = new ArrayList<>(poolByCategory.get(ci.categoryId));
            Collections.shuffle(pool, random);

            int toPick = Math.min(ci.targetCount, pool.size());
            if (toPick > 0) {
                selectedExercises.addAll(pool.subList(0, toPick));
            }
        }

        // 14. If still short (due to pool limitations), fill randomly from leftover pool
        if (selectedExercises.size() < totalQuestions) {
            List<Exercise> leftovers = new ArrayList<>(candidatePool);
            leftovers.removeAll(selectedExercises);
            Collections.shuffle(leftovers, random);

            int needMore = totalQuestions - selectedExercises.size();
            if (needMore > 0 && !leftovers.isEmpty()) {
                selectedExercises.addAll(
                        leftovers.subList(0, Math.min(needMore, leftovers.size()))
                );
            }
        }

        // 15. Map to DTO
        List<ExerciseResponseDto> questionDtos = selectedExercises.stream()
                .map(exerciseMapper::toDto)
                .toList();

        return new AttemptStartResponseDto(
                attempt.getId(),
                userId,
                lessonId,
                attemptNo,
                attempt.getStartedAt(),
                questionDtos
        );
    }

    @Transactional
    public AttemptResponseDto submitAttempt(UUID attemptId, List<AttemptDetailDto> submissions) {

        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt", "Id", attemptId));
        attempt.setAttemptStatus(AttemptStatus.SUBMITTED);

        int correctCount = 0;
        List<ResultDto> feedbacks = new ArrayList<>();

        Map<UUID, int[]> categoryStats = new HashMap<>();

        for (AttemptDetailDto dto : submissions) {

            Exercise exercise = exerciseRepository.findById(dto.exerciseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Exercise", "Id", dto.exerciseId()));

            String userAnswerJson = dto.userAnswerJson();
            String expectedAnswerJson = exercise.getCorrectAnswerJson();

            List<String> userAnswers = submissionUtil.parseAnswers(userAnswerJson, exercise);
            List<String> expectedAnswers = submissionUtil.parseAnswers(expectedAnswerJson, exercise);

            BigDecimal score = submissionUtil.calculateScore(exercise, userAnswerJson);

            boolean isCorrect = score.compareTo(BigDecimal.valueOf(50)) >= 0;
            if (isCorrect) correctCount++;

            AttemptDetail detail = new AttemptDetail();
            detail.setExercise(exercise);
            detail.setAttempt(attempt);
            detail.setUserAnswerJson(userAnswerJson);
            detail.setScore(score);
            attemptDetailRepository.save(detail);

            feedbacks.add(new ResultDto(
                    exercise.getId(),
                    exercise.getQuestion(),
                    exercise.getType().name(),
                    userAnswers,
                    expectedAnswers,
                    score
            ));

            UUID categoryId = exercise.getParent().getId();

            int[] stats = categoryStats.computeIfAbsent(categoryId, k -> new int[2]); // [0] = correct, [1] = wrong
            if (isCorrect) {
                stats[0]++; // correct
            } else {
                stats[1]++; // wrong
            }
        }

        BigDecimal finalScore = BigDecimal.valueOf((double) correctCount / submissions.size() * 100)
                .setScale(2, RoundingMode.HALF_UP);

        attempt.setScore(finalScore);
        attempt.setAttemptStatus(AttemptStatus.GRADED);
        attempt.setSubmittedAt(LocalDateTime.now());
        attemptRepository.save(attempt);

        UUID userId = attempt.getUser().getId();
        UUID lessonId = attempt.getLesson().getId();

        for (Map.Entry<UUID, int[]> entry : categoryStats.entrySet()) {
            UUID categoryId = entry.getKey();
            int[] stats = entry.getValue();
            int correct = stats[0];
            int wrong = stats[1];

            UserMasteryId masteryId = new UserMasteryId(userId, lessonId, categoryId);

            UserMastery mastery = userMasteryRepository.findById(masteryId)
                    .orElseGet(() -> {
                        UserMastery um = new UserMastery();
                        um.setId(masteryId);
                        um.setUser(attempt.getUser());
                        um.setLesson(attempt.getLesson());
                        um.setCorrectCount(0);
                        um.setWrongCount(0);
                        um.setAccuracy(0.0);
                        return um;
                    });

            int newCorrect = mastery.getCorrectCount() + correct;
            int newWrong = mastery.getWrongCount() + wrong;
            int total = newCorrect + newWrong;

            mastery.setCorrectCount(newCorrect);
            mastery.setWrongCount(newWrong);
            mastery.setAccuracy(total > 0 ? (double) newCorrect / total : 0.0);

            userMasteryRepository.save(mastery);
        }

        // --- 4) Return DTO as before ---
        return new AttemptResponseDto(
                attempt.getId(),
                attempt.getUser().getId(),
                attempt.getLesson().getId(),
                finalScore,
                attempt.getAttemptStatus().name(),
                attempt.getSubmittedAt(),
                feedbacks
        );
    }


}
