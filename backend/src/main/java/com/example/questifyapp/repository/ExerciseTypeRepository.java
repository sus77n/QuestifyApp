package com.example.questifyapp.repository;

import com.example.questifyapp.entity.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseTypeRepository extends JpaRepository<ExerciseType, Long> {
}
