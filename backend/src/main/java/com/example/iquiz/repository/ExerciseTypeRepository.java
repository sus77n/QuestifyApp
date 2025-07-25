package com.example.iquiz.repository;

import com.example.iquiz.entity.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseTypeRepository extends JpaRepository<ExerciseType, Long> {
}
