package com.example.iquiz.repository;

import com.example.iquiz.entity.UserExerciseRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserExerciseRatingRepository extends JpaRepository<UserExerciseRating, UUID> {
    Optional<UserExerciseRating> findByUserIdAndExerciseId(UUID userId, UUID exerciseId);
}

