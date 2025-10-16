package com.example.iquiz.repository;

import com.example.iquiz.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    List<Attempt> findByUserId(Long userId);
    List<Attempt> findByLessonId(Long lessonId);
    List<Attempt> findByUserIdAndLessonId(Long userId, Long lessonId);
}
