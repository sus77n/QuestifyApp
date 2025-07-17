package com.example.questifyapp.repository;

import com.example.questifyapp.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByUserId(long studentId);

    List<Submission> findByExerciseId(long exerciseId);

    Optional<Submission> findTopByUserIdAndExerciseIdOrderBySubmittedAtDesc(Long userId, Long exerciseId);

    Optional<Submission> findById(long id);

    @Query("""
                SELECT COUNT(DISTINCT s.exercise.id)
                FROM LearningUnit lu
                LEFT JOIN lu.exercises e
                LEFT JOIN Submission s ON s.exercise.id = e.id AND s.user.id = :userId AND s.score >= 50
                WHERE lu.id = :learningUnitId
            """)
    Long countPassedExercisesByUserIdAndLearningUnitId(Long userId, Long learningUnitId);

}
