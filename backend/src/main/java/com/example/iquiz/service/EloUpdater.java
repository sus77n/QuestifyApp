package com.example.iquiz.service;

import com.example.iquiz.entity.AttemptDetail;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.User;
import com.example.iquiz.entity.UserExerciseRating;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.UserExerciseRatingRepository;
import com.example.iquiz.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EloUpdater {

    private final ExerciseRepository exerciseRepository;
    private final UserExerciseRatingRepository ratingRepository;
    private final UserRepository userRepository;

    @Value("${iquiz.elo.k.user:32.0}")
    private double DEFAULT_K_USER;
    @Value("${iquiz.elo.k.item:16.0}")
    private double DEFAULT_K_ITEM;
    @Value("${iquiz.elo.k.user-global:16.0}")
    private double DEFAULT_K_USER_GLOBAL;
    @Value("${iquiz.elo.score-bucket:true}")
    private boolean SCORE_BUCKET;

    @Transactional
    public void performUpdate(UUID userId, AttemptDetail d) {
        UUID exerciseId = d.getExercise().getId();
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + exerciseId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        UserExerciseRating userRating = ratingRepository.findByUserIdAndExerciseId(userId, exerciseId)
                .orElseGet(() -> {
                    UserExerciseRating r = new UserExerciseRating();
                    r.setUser(user);
                    r.setExercise(exercise);
                    r.setRating(1000.0);
                    r.setAttempts(0);
                    r.setLastUpdated(LocalDateTime.now());
                    return ratingRepository.save(r);
                });

        double userR = userRating.getRating();
        double itemR = exercise.getDifficultyRating() == null ? 1000.0 : exercise.getDifficultyRating();

        double score = determineScore(d); // 0-1

        double expectedUser = 1.0 / (1.0 + Math.pow(10.0, (itemR - userR) / 400.0));
        double newUserR = userR + DEFAULT_K_USER * (score - expectedUser);

        double expectedItem = 1.0 / (1.0 + Math.pow(10.0, (userR - itemR) / 400.0));
        double itemScore = 1.0 - score;
        double newItemR = itemR + DEFAULT_K_ITEM * (itemScore - expectedItem);

        userRating.setRating(newUserR);
        userRating.setAttempts(userRating.getAttempts() + 1);
        userRating.setLastUpdated(LocalDateTime.now());
        ratingRepository.save(userRating);

        exercise.setDifficultyRating(newItemR);
        exercise.setTotalAttempts(exercise.getTotalAttempts() + 1);
        if (score >= 1.0) {
            exercise.setTotalCorrect(exercise.getTotalCorrect() + 1);
        }
        exerciseRepository.save(exercise);
    }

    private double determineScore(AttemptDetail d) {
        if (d.getScore() == null) return 0.0;
        try {
            double pct = d.getScore().doubleValue();
            if (pct >= 75.0) return 1.0;
            if (pct >= 50.0) return 0.5;
            return 0.0;
        } catch (Exception ex) {
            return 0.0;
        }
    }
}

