package com.example.iquiz.repository;

import com.example.iquiz.entity.LessonConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonConfigRepository extends JpaRepository<LessonConfig, Long> {
    Optional<LessonConfig> findByLessonId(Long lessonId);
    void deleteByLessonId(Long lessonId);

}
