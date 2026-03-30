package com.example.iquiz.service;

import com.example.iquiz.dto.SubmissionResult;
import com.example.iquiz.dto.attempt.AttemptDto;
import com.example.iquiz.dto.attempt.AttemptResponseDto;
import com.example.iquiz.dto.attempt.AttemptStartResponseDto;
import com.example.iquiz.dto.attempt.AttemptWithDetailsDto;
import com.example.iquiz.dto.exercise.ExerciseResponseDto;
import com.example.iquiz.dto.attemptDetail.AttemptDetailDto;
import com.example.iquiz.entity.*;
import com.example.iquiz.enums.AttemptStatus;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.mapper.AttemptMapper;
import com.example.iquiz.mapper.ExerciseMapper;
import com.example.iquiz.repository.*;

import com.example.iquiz.utility.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AttemptService {

    private final AttemptRepository attemptRepository;
    private final AttemptDetailRepository attemptDetailRepository;
    private final UserRepository userRepository;
    private final LearningUnitRepository learningUnitRepository;
    private final ExerciseMapper exerciseMapper;
    private final AttemptMapper attemptMapper;
    private final AttemptUtil attemptUtil;
    private final ExerciseUtil exerciseUtil;
    private final ProgressUtil progressUtil;
    private final ParticipantProgressService participantProgressService;

    public AttemptDto save(Attempt attempt) {
        return attemptMapper.toDto(attemptRepository.save(attempt));
    }

    public AttemptWithDetailsDto findById(UUID id) {
        Attempt attempt = attemptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt", "Id", id));
        return attemptMapper.toWithDetailsDto(attempt);
    }

    public List<AttemptDto> findByUser(UUID userId) {
        return attemptRepository.findByUserId(userId).stream()
                .map(attemptMapper::toDto)
                .toList();
    }

    public List<AttemptDto> findByLearningUnit(UUID learningUnitId) {
        return attemptRepository.findByLearningUnitId(learningUnitId).stream()
                .map(attemptMapper::toDto)
                .toList();
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

        int attemptNo = attemptRepository.countByUserIdAndLessonId(userId, lessonId) + 1;

        Attempt attempt = attemptUtil.create(user, lesson, attemptNo);
        attemptRepository.save(attempt);

        List<Exercise> selectedExercises =
                exerciseUtil.selectExercises(userId, lesson);

        LearningUnit course = learningUnitRepository.findRootByLearningUnitId(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "LessonId", lessonId));

        progressUtil.initIfAbsent(user, course);

        List<ExerciseResponseDto> questionDtos = selectedExercises.stream()
                .map(exerciseMapper::toDto)
                .toList();

        return new AttemptStartResponseDto(
                attempt.getId(),
                attempt.getUser().getId(),
                attempt.getLesson().getId(),
                attempt.getAttemptNo(),
                attempt.getStartedAt(),
                questionDtos
        );
    }

    public AttemptResponseDto submitAttempt(UUID attemptId, List<AttemptDetailDto> submissions) {

        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt", "Id", attemptId));

        Map<UUID, Exercise> exerciseMap = attemptUtil.loadExercises(submissions);

        SubmissionResult result = attemptUtil.evaluate(submissions, exerciseMap);

        for (AttemptDetail d : result.getDetails()) {
            d.setAttempt(attempt);
        }

        attemptDetailRepository.saveAll(result.getDetails());

        attempt.setScore(result.getFinalScore());
        attempt.setAttemptStatus(AttemptStatus.GRADED);
        attempt.setSubmittedAt(LocalDateTime.now());
        attemptRepository.save(attempt);

        attemptUtil.updateMastery(
                attempt.getUser().getId(),
                attempt.getLesson().getId(),
                result.getCategoryStats()
        );

        participantProgressService.updateProgress(attempt, result);

        return new AttemptResponseDto(
                attempt.getId(),
                attempt.getUser().getId(),
                attempt.getLesson().getId(),
                attempt.getScore(),
                attempt.getAttemptStatus().name(),
                attempt.getSubmittedAt(),
                result.getFeedbacks()
        );
    }
}
