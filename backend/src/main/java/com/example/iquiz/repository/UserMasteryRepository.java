package com.example.iquiz.repository;

import com.example.iquiz.entity.UserMastery;
import com.example.iquiz.entity.UserMasteryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMasteryRepository extends JpaRepository<UserMastery, UserMasteryId> {
    List<UserMastery> findByUserId(Long userId);
    List<UserMastery> findByLessonId(Long lessonId);
    List<UserMastery> findByExerciseTypeId(Long exerciseTypeId);
    List<UserMastery> findByUserIdAndLessonId(Long userId, Long lessonId);
}
