package com.example.iquiz.repository;

import com.example.iquiz.entity.LearningUnitType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LearningUnitTypeRepository extends JpaRepository<LearningUnitType, UUID> {
    Optional<LearningUnitType> findByName(String name);

    Optional<LearningUnitType> findByLevel(int level);
}
