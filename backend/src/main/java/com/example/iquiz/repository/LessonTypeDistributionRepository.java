package com.example.iquiz.repository;

import com.example.iquiz.entity.LessonTypeDistribution;
import com.example.iquiz.entity.LessonTypeDistributionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonTypeDistributionRepository extends JpaRepository<LessonTypeDistribution, LessonTypeDistributionId> {
    List<LessonTypeDistribution> findByLessonId(UUID lessonId);
}
