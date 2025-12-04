package com.example.iquiz.repository;

import com.example.iquiz.entity.AttemptDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttemptDetailRepository extends JpaRepository<AttemptDetail, UUID> {

    Optional<AttemptDetail> findById(UUID id);

    @Query("""
                SELECT COUNT(ad)
                FROM AttemptDetail ad
                WHERE ad.attempt.id = (
                    SELECT a.id
                    FROM Attempt a
                    WHERE a.lesson.id = :learningUnitId
                      AND a.user.id = :userId
                      AND a.score = (
                            SELECT MAX(a2.score)
                            FROM Attempt a2
                            WHERE a2.lesson.id = :learningUnitId
                              AND a2.user.id = :userId
                      )
                )
                AND ad.score >= 50
            """)
    Long countPassedExercisesByUserIdAndLearningUnitId(UUID userId, UUID learningUnitId);

    @Query("SELECT s FROM AttemptDetail s WHERE s.attempt.user.id = :userId AND s.exercise.parent.parent.id = :lessonId")
    List<AttemptDetail> findByUserAndLesson(UUID userId, UUID lessonId);

    @Query("""
                SELECT DISTINCT ad.exercise.id
                FROM AttemptDetail ad
                WHERE ad.attempt.user.id = :userId
                  AND ad.attempt.lesson.id = :lessonId
            """)
    List<UUID> findUsedExerciseIdsByUserAndLesson(UUID userId, UUID lessonId);


}
