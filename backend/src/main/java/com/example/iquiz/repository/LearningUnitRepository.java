package com.example.iquiz.repository;

import com.example.iquiz.dto.learningUnit.LearningUnitWithStatisticDto;
import com.example.iquiz.entity.LearningUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LearningUnitRepository extends JpaRepository<LearningUnit, UUID> {

    List<LearningUnit> findByTypeLevel(int typeLevel);

    Optional<LearningUnit> findByNameAndTypeId(String name, UUID typeId);

    List<LearningUnit> findAllByType_Name(String typeName);

    List<LearningUnit> findByType_IdAndCreatedBy_Id(UUID typeId, UUID createdById);

    @Query("""
                SELECT lu FROM LearningUnit lu
                LEFT JOIN FETCH lu.children
                WHERE lu.id = :id
            """)
    Optional<LearningUnit> findWithChildren(UUID id);

    @Query(value = """
            WITH RECURSIVE unit_tree AS (
                SELECT id, parent_id
                FROM learning_units
                WHERE id IN (:ids)
            
                UNION ALL
            
                SELECT lu.id, lu.parent_id
                FROM learning_units lu
                JOIN unit_tree ut ON lu.parent_id = ut.id
            )
            SELECT *
            FROM learning_units
            WHERE id IN (
                SELECT ut.id
                FROM unit_tree ut
                LEFT JOIN learning_units child ON child.parent_id = ut.id
                WHERE child.id IS NULL
            )
            """, nativeQuery = true)
    List<LearningUnit> findLeafNodesFromSubtree(@Param("ids") List<UUID> ids);

    @Query("""
            SELECT new com.example.iquiz.dto.learningUnit.LearningUnitWithStatisticDto(
                c.id,
                c.name,
                c.code,
                c.description,
                t.name,
                c.status,
                c.createdAt,
                CONCAT(u.firstName,' ',u.lastName),
                c.parent.id,
                null,
                p.completedExercises,
                p.totalExercises
            )
            FROM ParticipantProgress p
            JOIN p.course c
            JOIN c.type t
            LEFT JOIN c.createdBy u
            WHERE p.user.id = :userId
            AND p.completedExercises < p.totalExercises
            """)
    List<LearningUnitWithStatisticDto> findIncompleteCoursesWithStatistics(UUID userId);

    @Query(value = """
            WITH RECURSIVE unit_tree AS (
                SELECT id
                FROM learning_units
                WHERE id = :id
            
                UNION ALL
            
                SELECT lu.id
                FROM learning_units lu
                JOIN unit_tree ut ON lu.parent_id = ut.id
            )
            SELECT COUNT(e.id)
            FROM exercises e
            JOIN unit_tree ut ON e.learning_unit_id = ut.id
            """, nativeQuery = true)
    long countExercisesUnderLearningUnit(UUID id);

    @Query(value = """
    WITH best_attempt AS (
        SELECT a.id
        FROM attempts a
        WHERE a.lesson_id = :lessonId
          AND a.user_id = :userId
          AND a.attempt_status = 'SUBMITTED'
        ORDER BY a.score DESC
        LIMIT 1
    )
    
    SELECT COUNT(*)
    FROM attempt_details ad
    JOIN best_attempt ba ON ad.attempt_id = ba.id
    WHERE ad.score BETWEEN 50 AND 100
    """, nativeQuery = true)
    long countPassedExercisesInBestAttempt(UUID lessonId, UUID userId);

    @Query(value = """
                WITH RECURSIVE hierarchy AS (
                    SELECT * FROM learning_units WHERE id = :id
                    UNION ALL
                    SELECT lu.* FROM learning_units lu
                    JOIN hierarchy h ON lu.id = h.parent_id
                )
                SELECT * FROM hierarchy
                WHERE parent_id IS NULL
                LIMIT 1
            """, nativeQuery = true)
    Optional<LearningUnit> findRootByLearningUnitId(@Param("id") UUID id);
}
