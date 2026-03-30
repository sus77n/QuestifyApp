package com.example.iquiz.service;

import com.example.iquiz.dto.ProgressDTO;
import com.example.iquiz.dto.SubmissionResult;
import com.example.iquiz.entity.Attempt;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.entity.ParticipantProgress;
import com.example.iquiz.enums.UserProgress;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.mapper.ParticipantProgressMapper;
import com.example.iquiz.repository.LearningUnitRepository;
import com.example.iquiz.repository.LessonConfigRepository;
import com.example.iquiz.repository.ParticipantProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParticipantProgressService {
    @Autowired
    private final ParticipantProgressRepository participantProgressRepository;
    @Autowired
    private final ParticipantProgressMapper participantProgressMapper;
    @Autowired
    private LearningUnitRepository learningUnitRepository;
    @Autowired
    private LessonConfigRepository lessonConfigRepository;

    public List<ProgressDTO> findIncompletedCoursesByParticipantId(UUID participantId) {
        if (participantId == null) {
            throw new IllegalArgumentException("Participant ID cannot be null");
        }

        List<ParticipantProgress> progressList = participantProgressRepository.findAllByUser_Id(participantId);
        return progressList.stream()
                .map(participantProgressMapper::toDto)
                .toList();
    }

    public List<ProgressDTO> findCompletedCoursesByParticipantId(UUID participantId) {
        if (participantId == null) {
            throw new IllegalArgumentException("Participant ID cannot be null");
        }

        List<ParticipantProgress> progressList = participantProgressRepository.findAllByUser_Id(participantId);
        return progressList.stream()
                .filter(participantProgress -> {
                    return participantProgress.getProgressPercent().intValue() >= 100;
                })
                .map(participantProgressMapper::toDto)
                .toList();
    }

    public void updateProgress(Attempt attempt, SubmissionResult result) {

        UUID userId = attempt.getUser().getId();
        LearningUnit course = learningUnitRepository.findRootByLearningUnitId(attempt.getLesson().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "Children Id", attempt.getLesson().getId()));

        ParticipantProgress progress =
                participantProgressRepository.findByUserIdAndCourseId(userId, course.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Participant progress", "User and Course", userId + " " + course));

        progress.setAttemptCount(progress.getAttemptCount() + 1);
        progress.setCompletedExercises(result.getCorrectCount());

        if (progress.getBestScore() == null ||
                attempt.getScore().compareTo(progress.getBestScore()) > 0) {
            progress.setBestScore(attempt.getScore());
        }

        if (progress.getTotalExercises() == 0)
            progress.setTotalExercises(
                    lessonConfigRepository.countQuestionsPerAttemptInLearningUnitTree(
                            course.getId()
                    ));

        BigDecimal percent = BigDecimal.valueOf(progress.getCompletedExercises())
                .divide(BigDecimal.valueOf(progress.getTotalExercises()), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        progress.setProgressPercent(percent);

        if (percent.compareTo(BigDecimal.valueOf(100)) >= 0) {
            progress.setStatus(UserProgress.COMPLETED);
        } else if (progress.getAttemptCount() > 0) {
            progress.setStatus(UserProgress.IN_PROGRESS);
        } else {
            progress.setStatus(UserProgress.NOT_STARTED);
        }

        progress.setLastAttempt(attempt);
        progress.setLastActivityAt(LocalDateTime.now());

        participantProgressRepository.save(progress);
    }
}
