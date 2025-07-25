package com.example.iquiz.repository;

import com.example.iquiz.entity.LearningUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningUnitRepository extends JpaRepository<LearningUnit, Long> {

    LearningUnit findByName(String name);

    List<LearningUnit> findByTypeLevel(int typeLevel);
}
