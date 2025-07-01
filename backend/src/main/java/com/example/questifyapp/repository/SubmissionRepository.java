package com.example.questifyapp.repository;

import com.example.questifyapp.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByStudentId(long studentId);

    List<Submission> findByExerciseId(long exerciseId);

    Optional<Submission> findByStudentIdAndExerciseId(long studentId, long exerciseId);

    Optional<Submission> findById(long id);

    @Query("SELECT COUNT(s) > 0 FROM Submission s WHERE s.exercise.id = :exerciseId AND s.score BETWEEN 50 and 100")
    boolean existsByExerciseIdAndScoreBetween50And100(Long exerciseId);
}
