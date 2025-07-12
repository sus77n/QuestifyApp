package com.example.questifyapp.repository;

import com.example.questifyapp.entity.LearningUnitType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningUnitTypeRepository extends JpaRepository<LearningUnitType, Long> {
    LearningUnitType findByName(String name);

    LearningUnitType findByLevel(int level);
}
