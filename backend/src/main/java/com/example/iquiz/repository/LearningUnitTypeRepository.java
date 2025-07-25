package com.example.iquiz.repository;

import com.example.iquiz.entity.LearningUnitType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningUnitTypeRepository extends JpaRepository<LearningUnitType, Long> {
    LearningUnitType findByName(String name);

    LearningUnitType findByLevel(int level);
}
