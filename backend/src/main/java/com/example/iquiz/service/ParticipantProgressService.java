package com.example.iquiz.service;

import com.example.iquiz.dto.ProgressDTO;
import com.example.iquiz.dto.SubmissionResult;
import com.example.iquiz.entity.Attempt;
import com.example.iquiz.entity.ParticipantProgress;
import com.example.iquiz.enums.UserProgress;
import com.example.iquiz.mapper.ParticipantProgressMapper;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.ParticipantProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ExerciseRepository exerciseRepository;

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

    @Transactional
    public void updateProgress(Attempt attempt, SubmissionResult result) {

        UUID userId = attempt.getUser().getId();
        UUID courseId = attempt.getLesson().getParent().getId();

        ParticipantProgress progress =
                participantProgressRepository.findByUserIdAndCourseId(userId, courseId)
                        .orElseGet(() -> createNewProgress(attempt));

        progress.setAttemptCount(progress.getAttemptCount() + 1);

        progress.setCompletedExercises(result.getCorrectCount());

        if (progress.getBestScore() == null ||
                attempt.getScore().compareTo(progress.getBestScore()) > 0) {
            progress.setBestScore(attempt.getScore());
        }

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

    private ParticipantProgress createNewProgress(Attempt attempt) {

        int totalExercises = exerciseRepository.countExercisesInLearningUnitTree(
                attempt.getLesson().getParent().getId()
        );

        return ParticipantProgress.builder()
                .user(attempt.getUser())
                .course(attempt.getLesson().getParent())
                .attemptCount(0)
                .completedExercises(0)
                .totalExercises(totalExercises)
                .status(UserProgress.IN_PROGRESS)
                .progressPercent(BigDecimal.ZERO)
                .build();
    }
}
