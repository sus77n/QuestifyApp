package com.example.iquiz.service;

import com.example.iquiz.dto.attempt.AttemptResponseDto;
import com.example.iquiz.dto.attempt.AttemptStartResponseDto;
import com.example.iquiz.dto.submission.SubmissionDto;
import com.example.iquiz.entity.*;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.repository.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttemptService {

    private final AttemptRepository attemptRepository;
    private final ExerciseRepository exerciseRepository;
    private final OptionRepository optionRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final LearningUnitRepository learningUnitRepository;
    private final LessonConfigRepository lessonConfigRepository;
    private final LessonTypeDistributionRepository lessonTypeDistributionRepository;


    public Attempt save(Attempt attempt) {
        return attemptRepository.save(attempt);
    }

    public Attempt findById(Long id) {
        return attemptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt", "id", id));
    }

    public List<Attempt> findByUser(Long userId) {
        return attemptRepository.findByUserId(userId);
    }

    public List<Attempt> findByLesson(Long lessonId) {
        return attemptRepository.findByLessonId(lessonId);
    }

    public List<Attempt> findByUserAndLesson(Long userId, Long lessonId) {
        return attemptRepository.findByUserIdAndLessonId(userId, lessonId);
    }

    public void delete(Long id) {
        attemptRepository.deleteById(id);
    }

    @Transactional
    public AttemptStartResponseDto startAttempt(Long userId, Long lessonId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LearningUnit lesson = learningUnitRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        int attemptNo = attemptRepository.findByUserIdAndLessonId(userId, lessonId).size() + 1;

        Attempt attempt = new Attempt();
        attempt.setUser(user);
        attempt.setLesson(lesson);
        attempt.setAttemptNo(attemptNo);
        attempt.setStartedAt(LocalDateTime.now());
        attemptRepository.save(attempt);

        // --- 1. Lấy config ---
        LessonConfig config = lessonConfigRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("LessonConfig not found for lessonId: ", "" ,lessonId));

        int totalQuestions = config.getQuestionsPerAttempt();

        List<LessonTypeDistribution> distributions = lessonTypeDistributionRepository.findByLessonId(lessonId);

        List<Exercise> selectedExercises = new ArrayList<>();
        Random random = new Random();

        // --- 2. Nếu noRepeatScope = true -> lấy danh sách exercise đã dùng ---
        Set<Long> usedExerciseIds = new HashSet<>();
        if (config.isNoRepeatScope()) {
            List<Submission> pastSubmissions = submissionRepository.findByUserAndLesson(userId, lessonId);
            usedExerciseIds = pastSubmissions.stream()
                    .map(s -> s.getExercise().getId())
                    .collect(Collectors.toSet());
        }

        final Set<Long> finalUsedExerciseIds = usedExerciseIds; // fix lambda issue

        // --- 3. Chọn câu hỏi theo phân phối ---
        for (LessonTypeDistribution dist : distributions) {
            List<Exercise> pool = exerciseRepository.findByParent_IdAndExerciseCategory_Id(
                    lessonId, dist.getExerciseCategory().getId()
            );

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
            List<Exercise> all = exerciseRepository.findByParent_Id(lessonId);

            if (config.isNoRepeatScope()) {
                all.removeIf(ex -> finalUsedExerciseIds.contains(ex.getId()));
            }

            all.removeAll(selectedExercises);
            Collections.shuffle(all, random);

            int needMore = totalQuestions - selectedExercises.size();

            selectedExercises.addAll(all.subList(0, Math.min(needMore, all.size())));
        }

        // --- 5. Convert sang DTO ---
        List<AttemptStartResponseDto.QuestionDto> questionDtos = selectedExercises.stream()
                .map(ex -> new AttemptStartResponseDto.QuestionDto(
                        ex.getId(),
                        ex.getQuestion(),
                        ex.getOptions().stream()
                                .map(op -> new AttemptStartResponseDto.OptionDto(op.getId(), op.getText()))
                                .toList()
                ))
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
    public AttemptResponseDto submitAttempt(Long attemptId, List<SubmissionDto> submissions) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new EntityNotFoundException("Attempt not found"));

        int correctCount = 0;
        List<AttemptResponseDto.FeedbackDto> feedbacks = new java.util.ArrayList<>();

        for (SubmissionDto dto : submissions) {
            Exercise exercise = exerciseRepository.findById(dto.exerciseId())
                    .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

            boolean correct = false;
            String expectedAnswer = exercise.getAnswer();
            if (expectedAnswer == null) {
                Option option = optionRepository.findCorrectOptionByExerciseId(exercise.getId());
                if (option != null) {
                    expectedAnswer = option.getText() + ". \n" + option.getExplanation();
                }
            }
            String userAnswer = dto.answer();

            Option selectedOption = null;
            if (dto.selectedOptionId() != null) {
                selectedOption = optionRepository.findById(dto.selectedOptionId())
                        .orElseThrow(() -> new EntityNotFoundException("Option not found"));
                correct = selectedOption.isCorrect();
                userAnswer = selectedOption.getText();
            } else if (dto.answer() != null) {
                correct = dto.answer().trim().equalsIgnoreCase(expectedAnswer.trim());
            }

            if (correct) correctCount++;

            // Lưu submission
            Submission submission = new Submission();
            submission.setExercise(exercise);
            submission.setUser(attempt.getUser());
            submission.setAttempt(attempt);
            submission.setAnswer(dto.answer());
            submission.setSelectedOption(selectedOption);
            submission.setScore(correct ? BigDecimal.ONE : BigDecimal.ZERO);
            submission.setSubmittedAt(LocalDateTime.now());
            submissionRepository.save(submission);

            // Thêm feedback
            feedbacks.add(new AttemptResponseDto.FeedbackDto(
                    exercise.getId(),
                    exercise.getQuestion(),
                    correct,
                    userAnswer,
                    expectedAnswer
            ));
        }

        // Tính điểm tổng
        BigDecimal score = BigDecimal.valueOf((double) correctCount / submissions.size() * 100);
        attempt.setScore(score);
        attempt.setSubmittedAt(LocalDateTime.now());
        attemptRepository.save(attempt);

        return new AttemptResponseDto(
                attempt.getId(),
                attempt.getUser().getId(),
                attempt.getLesson().getId(),
                score,
                attempt.getStatus() == 1 ? "PASSED" : "FAILED",
                attempt.getSubmittedAt(),
                feedbacks
        );
    }

}
