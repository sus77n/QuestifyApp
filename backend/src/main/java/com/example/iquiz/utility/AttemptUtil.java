package com.example.iquiz.utility;

import com.example.iquiz.dto.CatInfo;
import com.example.iquiz.dto.SubmissionResult;
import com.example.iquiz.dto.attemptDetail.AttemptDetailDto;
import com.example.iquiz.dto.attemptDetail.ResultDto;
import com.example.iquiz.entity.*;
import com.example.iquiz.enums.AttemptStatus;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.LearningUnitRepository;
import com.example.iquiz.repository.UserMasteryRepository;
import com.example.iquiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttemptUtil {

    private final ExerciseRepository exerciseRepository;
    private final SubmissionUtil submissionUtil;
    private final UserMasteryRepository userMasteryRepository;
    private final LearningUnitRepository learningUnitRepository;
    private final UserRepository userRepository;

    public Attempt create(User user, LearningUnit lesson, int attemptNo) {
        Attempt attempt = new Attempt();
        attempt.setUser(user);
        attempt.setLesson(lesson);
        attempt.setAttemptNo(attemptNo);
        attempt.setAttemptStatus(AttemptStatus.IN_PROGRESS);
        return attempt;
    }

    public Map<UUID, Exercise> loadExercises(List<AttemptDetailDto> submissions) {

        List<UUID> ids = submissions.stream()
                .map(AttemptDetailDto::exerciseId)
                .toList();

        return exerciseRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(Exercise::getId, Function.identity()));
    }

    public SubmissionResult evaluate(
            List<AttemptDetailDto> submissions,
            Map<UUID, Exercise> exerciseMap
    ) {

        int correctCount = 0;
        int wrongCount = 0;
        List<ResultDto> feedbacks = new ArrayList<>();
        List<AttemptDetail> details = new ArrayList<>();
        Map<UUID, int[]> categoryStats = new HashMap<>();

        for (AttemptDetailDto dto : submissions) {

            Exercise exercise = exerciseMap.get(dto.exerciseId());

            String userAnswerJson = dto.userAnswerJson();

            List<String> userAnswers =
                    submissionUtil.parseAnswers(userAnswerJson, exercise);
            List<String> expectedAnswers =
                    submissionUtil.parseAnswers(exercise.getCorrectAnswerJson(), exercise);

            BigDecimal score =
                    submissionUtil.calculateScore(exercise, userAnswerJson);

            boolean isCorrect = score.compareTo(BigDecimal.valueOf(50)) >= 0;
            if (isCorrect) {
                correctCount++;
            }
            else {
                wrongCount++;
            }

            AttemptDetail detail = new AttemptDetail();
            detail.setExercise(exercise);
            detail.setUserAnswerJson(userAnswerJson);
            detail.setScore(score);

            details.add(detail);

            feedbacks.add(new ResultDto(
                    exercise.getId(),
                    exercise.getQuestion(),
                    exercise.getType().name(),
                    userAnswers,
                    expectedAnswers,
                    score
            ));

            UUID categoryId = exercise.getParent().getId();
            int[] stats = categoryStats.computeIfAbsent(categoryId, k -> new int[2]);

            if (isCorrect) stats[0]++;
            else stats[1]++;
        }

        BigDecimal finalScore = BigDecimal.valueOf(
                (double) correctCount / submissions.size() * 100
        ).setScale(2, RoundingMode.HALF_UP);

        return new SubmissionResult(details, feedbacks, categoryStats, finalScore, correctCount, wrongCount);
    }

    public void updateMastery(
            UUID userId,
            UUID lessonId,
            Map<UUID, int[]> categoryStats
    ) {

        LearningUnit lesson = learningUnitRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        for (Map.Entry<UUID, int[]> entry : categoryStats.entrySet()) {

            UUID categoryId = entry.getKey();
            int correct = entry.getValue()[0];
            int wrong = entry.getValue()[1];

            LearningUnit category = learningUnitRepository.getReferenceById(categoryId);

            UserMasteryId id = new UserMasteryId(userId, lessonId, categoryId);

            UserMastery mastery = userMasteryRepository.findById(id)
                    .orElseGet(() -> {
                        UserMastery m = new UserMastery();
                        m.setId(id);

                        m.setUser(user);
                        m.setLesson(lesson);
                        m.setCategory(category);

                        m.setCorrectCount(0);
                        m.setWrongCount(0);
                        m.setAccuracy(0.0);
                        return m;
                    });

            int newCorrect = mastery.getCorrectCount() + correct;
            int newWrong = mastery.getWrongCount() + wrong;
            int total = newCorrect + newWrong;

            mastery.setCorrectCount(newCorrect);
            mastery.setWrongCount(newWrong);
            mastery.setAccuracy(total > 0 ? (double) newCorrect / total : 0.0);

            userMasteryRepository.save(mastery);
        }
    }
}
