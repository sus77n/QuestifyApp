package com.example.questifyapp.repository;

import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.entity.Submission;
import com.example.questifyapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // Existing methods (keeping them as-is)
    List<Submission> findByStudentId(Long studentId);

    List<Submission> findByExerciseId(Long exerciseId);

    Optional<Submission> findByStudentIdAndExerciseId(Long studentId, Long exerciseId);

    @Query("SELECT COUNT(s) > 0 FROM Submission s WHERE s.exercise.id = :exerciseId AND s.score BETWEEN 50 and 100")
    boolean existsByExerciseIdAndScoreBetween50And100(Long exerciseId);

    // New CRUD and query methods

    // Find submissions by student (User entity)
    List<Submission> findByStudent(User student);

    // Find submissions by exercise (Exercise entity)
    List<Submission> findByExercise(Exercise exercise);

    // Find submissions by student and exercise (entities)
    Optional<Submission> findByStudentAndExercise(User student, Exercise exercise);

    // Find submissions by score
    List<Submission> findByScore(BigDecimal score);

    // Find submissions by score range
    List<Submission> findByScoreBetween(BigDecimal minScore, BigDecimal maxScore);

    // Find submissions by score greater than
    List<Submission> findByScoreGreaterThan(BigDecimal score);

    // Find submissions by score greater than or equal
    List<Submission> findByScoreGreaterThanEqual(BigDecimal score);

    // Find submissions by score less than
    List<Submission> findByScoreLessThan(BigDecimal score);

    // Find submissions by score less than or equal
    List<Submission> findByScoreLessThanEqual(BigDecimal score);

    // Find submissions by text containing (search)
    List<Submission> findByTextContainingIgnoreCase(String text);

    // Find submissions by submitted date after
    List<Submission> findBySubmittedAtAfter(LocalDateTime date);

    // Find submissions by submitted date before
    List<Submission> findBySubmittedAtBefore(LocalDateTime date);

    // Find submissions by submitted date between
    List<Submission> findBySubmittedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find submissions by student and score range
    List<Submission> findByStudentAndScoreBetween(User student, BigDecimal minScore, BigDecimal maxScore);

    // Find submissions by student ID and score range
    List<Submission> findByStudentIdAndScoreBetween(Long studentId, BigDecimal minScore, BigDecimal maxScore);

    // Find submissions by exercise and score range
    List<Submission> findByExerciseAndScoreBetween(Exercise exercise, BigDecimal minScore, BigDecimal maxScore);

    // Find submissions by exercise ID and score range
    List<Submission> findByExerciseIdAndScoreBetween(Long exerciseId, BigDecimal minScore, BigDecimal maxScore);

    // Ordering methods
    List<Submission> findByStudentIdOrderBySubmittedAtDesc(Long studentId);

    List<Submission> findByExerciseIdOrderByScoreDesc(Long exerciseId);

    List<Submission> findByStudentIdOrderByScoreDesc(Long studentId);

    List<Submission> findAllByOrderBySubmittedAtDesc();

    List<Submission> findAllByOrderByScoreDesc();

    // Count methods
    long countByStudent(User student);

    long countByStudentId(Long studentId);

    long countByExercise(Exercise exercise);

    long countByExerciseId(Long exerciseId);

    long countByScore(BigDecimal score);

    long countByScoreBetween(BigDecimal minScore, BigDecimal maxScore);

    long countByScoreGreaterThan(BigDecimal score);

    long countByScoreGreaterThanEqual(BigDecimal score);

    // Custom queries

    // Search submissions by text
    @Query("SELECT s FROM Submission s WHERE LOWER(s.text) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Submission> searchByText(@Param("searchTerm") String searchTerm);

    // Find passing submissions (score >= 50)
    @Query("SELECT s FROM Submission s WHERE s.score >= 50")
    List<Submission> findPassingSubmissions();

    // Find failing submissions (score < 50)
    @Query("SELECT s FROM Submission s WHERE s.score < 50")
    List<Submission> findFailingSubmissions();

    // Find perfect submissions (score = 100)
    @Query("SELECT s FROM Submission s WHERE s.score = 100")
    List<Submission> findPerfectSubmissions();

    // Get average score by student
    @Query("SELECT AVG(s.score) FROM Submission s WHERE s.student.id = :studentId")
    BigDecimal getAverageScoreByStudentId(@Param("studentId") Long studentId);

    // Get average score by exercise
    @Query("SELECT AVG(s.score) FROM Submission s WHERE s.exercise.id = :exerciseId")
    BigDecimal getAverageScoreByExerciseId(@Param("exerciseId") Long exerciseId);

    // Get highest score by student
    @Query("SELECT MAX(s.score) FROM Submission s WHERE s.student.id = :studentId")
    BigDecimal getHighestScoreByStudentId(@Param("studentId") Long studentId);

    // Get highest score by exercise
    @Query("SELECT MAX(s.score) FROM Submission s WHERE s.exercise.id = :exerciseId")
    BigDecimal getHighestScoreByExerciseId(@Param("exerciseId") Long exerciseId);

    // Count passing submissions by student
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.student.id = :studentId AND s.score >= 50")
    long countPassingSubmissionsByStudentId(@Param("studentId") Long studentId);

    // Count passing submissions by exercise
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.exercise.id = :exerciseId AND s.score >= 50")
    long countPassingSubmissionsByExerciseId(@Param("exerciseId") Long exerciseId);

    // Find latest submission by student and exercise
    @Query("SELECT s FROM Submission s WHERE s.student.id = :studentId AND s.exercise.id = :exerciseId ORDER BY s.submittedAt DESC")
    List<Submission> findLatestSubmissionByStudentAndExercise(@Param("studentId") Long studentId, @Param("exerciseId") Long exerciseId);

    // Find submissions by exercise type
    @Query("SELECT s FROM Submission s JOIN s.exercise e WHERE e.type = :exerciseType")
    List<Submission> findByExerciseType(@Param("exerciseType") String exerciseType);

    // Find top submissions by exercise (highest scores)
    @Query("SELECT s FROM Submission s WHERE s.exercise.id = :exerciseId ORDER BY s.score DESC")
    List<Submission> findTopSubmissionsByExerciseId(@Param("exerciseId") Long exerciseId);
}
