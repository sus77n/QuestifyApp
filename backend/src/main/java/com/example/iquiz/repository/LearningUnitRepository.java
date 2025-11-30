package com.example.iquiz.repository;

import com.example.iquiz.entity.LearningUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LearningUnitRepository extends JpaRepository<LearningUnit, UUID> {

    List<LearningUnit> findByTypeLevel(int typeLevel);

    Optional<LearningUnit> findByNameAndTypeId(String name, UUID typeId);

    List<LearningUnit> findAllByType_Name(String typeName);

    List<LearningUnit> findByType_IdAndCreatedBy_Id(UUID typeId, UUID createdById);
}
