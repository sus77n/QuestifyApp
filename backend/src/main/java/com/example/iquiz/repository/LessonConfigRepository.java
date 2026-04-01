package com.example.iquiz.repository;

import com.example.iquiz.entity.LessonConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonConfigRepository extends JpaRepository<LessonConfig, UUID> {
    Optional<LessonConfig> findByLessonId(UUID lessonId);

    void deleteByLessonId(UUID lessonId);

    @Query(value = """
            WITH RECURSIVE UnitTree AS (
                SELECT id, parent_id
                FROM learning_units
                WHERE id = :learningUnitId
                UNION ALL
                SELECT lu.id, lu.parent_id
                FROM learning_units lu
                JOIN UnitTree t ON lu.parent_id = t.id
            )
            SELECT COALESCE(SUM(lc.questions_per_attempt), 0)
            FROM lesson_configs lc
            JOIN UnitTree t ON lc.lesson_id = t.id
            """, nativeQuery = true)
    int countQuestionsPerAttemptInLearningUnitTree(UUID learningUnitId);
}
