package com.example.iquiz.service;

import com.example.iquiz.dto.attempt.AttemptResponseDto;
import com.example.iquiz.dto.attempt.AttemptStartResponseDto;
import com.example.iquiz.dto.attemptDetail.ResultDto;
import com.example.iquiz.dto.exercise.ExerciseResponseDto;
import com.example.iquiz.dto.attemptDetail.AttemptDetailDto;
import com.example.iquiz.entity.*;
import com.example.iquiz.enums.AttemptStatus;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.mapper.ExerciseMapper;
import com.example.iquiz.repository.*;

import com.example.iquiz.utility.ExerciseTypeUtil;
import com.example.iquiz.utility.SubmissionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
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
    private final LessonTypeDistributionRepository lessonTypeDistributionRepository;
    private final ExerciseMapper exerciseMapper;


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

        // --- 1. Lấy config ---
        LessonConfig config = lessonConfigRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson Config", "Id", lessonId));


        // --- 2. Nếu noRepeatScope = true -> lấy danh sách exercise đã dùng ---
        Set<UUID> usedExerciseIds = new HashSet<>();
        if (config.isNoRepeatScope()) {
            List<AttemptDetail> pastAttemptDetails = attemptDetailRepository.findByUserAndLesson(userId, lessonId);
            usedExerciseIds = pastAttemptDetails.stream()
                    .map(s -> s.getExercise().getId())
                    .collect(Collectors.toSet());
        }

        final Set<UUID> finalUsedExerciseIds = usedExerciseIds; // fix lambda issue

        List<Exercise> selectedExercises = new ArrayList<>();
        Random random = new Random();
        int totalQuestions = config.getQuestionsPerAttempt();
        // --- 3. Chọn câu hỏi theo phân phối ---
        List<LessonTypeDistribution> distributions = lessonTypeDistributionRepository.findByLessonId(lessonId);
        for (LessonTypeDistribution dist : distributions) {
            // 123
            List<Exercise> pool = exerciseRepository.findAll();

            // Nếu có noRepeatScope, loại bỏ câu hỏi đã dùng
            if (config.isNoRepeatScope()) {
                pool.removeIf(ex -> finalUsedExerciseIds.contains(ex.getId()));
            }

            // Tính số câu cần lấy
            int toPick = Math.min(pool.size(), Math.max(dist.getMinPerAttempt(),
                    (int) Math.round(dist.getBaseWeight().doubleValue() * totalQuestions)));

            toPick = Math.min(toPick, dist.getMaxPerAttempt());

            Collections.shuffle(pool, random);
            selectedExercises.addAll(pool.subList(0, Math.min(toPick, pool.size())));
        }

        // --- 4. Nếu tổng chưa đủ (do min/max/noRepeatScope giới hạn), bổ sung ngẫu nhiên ---
        if (selectedExercises.size() < totalQuestions) {
            List<Exercise> all = exerciseRepository.findByParent_IdIn(
                    lesson.getChildren().stream()
                            .map(LearningUnit::getId)
                            .toList()
            );

            all.removeIf(ex -> finalUsedExerciseIds.contains(ex.getId()));
            all.removeAll(selectedExercises);
            Collections.shuffle(all, random);

            int needMore = totalQuestions - selectedExercises.size();

            selectedExercises.addAll(all.subList(0, Math.min(needMore, all.size())));
        }

        // --- 5. Convert sang DTO ---
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

        for (AttemptDetailDto dto : submissions) {

            Exercise exercise = exerciseRepository.findById(dto.exerciseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Exercise", "Id", dto.exerciseId()));

            String userAnswerJson = dto.userAnswerJson();
            String expectedAnswerJson = exercise.getCorrectAnswerJson();

            List<String> userAnswers = submissionUtil.parseAnswers(userAnswerJson, exercise);
            List<String> expectedAnswers = submissionUtil.parseAnswers(expectedAnswerJson, exercise);

            BigDecimal score = submissionUtil.calculateScore(exercise, userAnswerJson);
            if (score.compareTo(BigDecimal.valueOf(50)) >= 0) correctCount++;

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
        }

        BigDecimal finalScore = BigDecimal.valueOf((double) correctCount / submissions.size() * 100)
                .setScale(2, RoundingMode.HALF_UP);

        attempt.setScore(finalScore);
        attempt.setAttemptStatus(AttemptStatus.GRADED);
        attempt.setSubmittedAt(LocalDateTime.now());
        attemptRepository.save(attempt);

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
