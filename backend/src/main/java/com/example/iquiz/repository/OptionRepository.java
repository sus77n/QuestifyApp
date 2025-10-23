package com.example.iquiz.repository;

import com.example.iquiz.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OptionRepository extends JpaRepository<Option, Long> {

    @Query("SELECT o FROM Option o WHERE o.exercise.id = :exerciseId AND o.isCorrect = true")
    Option findCorrectOptionByExerciseId(Long exerciseId);
}
