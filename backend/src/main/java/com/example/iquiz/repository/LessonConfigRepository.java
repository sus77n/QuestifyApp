package com.example.iquiz.repository;

import com.example.iquiz.entity.LessonConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonConfigRepository extends JpaRepository<LessonConfig, UUID> {
    Optional<LessonConfig> findByLessonId(UUID lessonId);
    void deleteByLessonId(UUID lessonId);

}
