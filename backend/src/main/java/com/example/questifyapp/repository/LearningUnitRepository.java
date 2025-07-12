package com.example.questifyapp.repository;

import com.example.questifyapp.entity.LearningUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningUnitRepository extends JpaRepository<LearningUnit, Long> {

    LearningUnit findByName(String name);

    List<LearningUnit> findByTypeLevel(int typeLevel);
}
