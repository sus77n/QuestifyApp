package com.example.iquiz.repository;

import com.example.iquiz.entity.ExerciseCategoryDistribution;
import com.example.iquiz.entity.ExerciseCategoryDistributionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExerciseCategoryDistributionRepository extends JpaRepository<ExerciseCategoryDistribution, ExerciseCategoryDistributionId> {
    List<ExerciseCategoryDistribution> findByParentLessonId(UUID lessonId);
}
