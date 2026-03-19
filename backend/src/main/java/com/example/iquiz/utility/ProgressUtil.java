package com.example.iquiz.utility;

import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.entity.ParticipantProgress;
import com.example.iquiz.entity.User;
import com.example.iquiz.enums.UserProgress;
import com.example.iquiz.repository.ParticipantProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ProgressUtil {

    private final ParticipantProgressRepository participantProgressRepository;

    public ParticipantProgress initIfAbsent(User user, LearningUnit course) {

        return participantProgressRepository.findByUserIdAndCourseId(user.getId(), course.getId())
                .orElseGet(() -> {
                    ParticipantProgress progress = new ParticipantProgress();
                    progress.setUser(user);
                    progress.setCourse(course);
                    progress.setAttemptCount(0);
                    progress.setCompletedExercises(0);
                    progress.setTotalExercises(0);
                    progress.setProgressPercent(BigDecimal.ZERO);
                    progress.setBestScore(BigDecimal.ZERO);
                    progress.setStatus(UserProgress.IN_PROGRESS);

                    return participantProgressRepository.save(progress);
                });
    }
}
