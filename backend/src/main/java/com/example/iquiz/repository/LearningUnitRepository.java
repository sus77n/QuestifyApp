package com.example.iquiz.repository;

import com.example.iquiz.entity.LearningUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningUnitRepository extends JpaRepository<LearningUnit, Long> {

    LearningUnit findByName(String name);

    List<LearningUnit> findByTypeLevel(int typeLevel);

    Optional<LearningUnit> findByNameAndTypeId(String name, Long typeId);

    List<LearningUnit> findAllByType_Name(String typeName);
}
