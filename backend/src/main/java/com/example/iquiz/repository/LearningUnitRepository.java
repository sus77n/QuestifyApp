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
            WITH RECURSIVE unit_tree AS (
                SELECT id
                FROM learning_units
                WHERE id = :learningUnitId
            
                UNION ALL
            
                SELECT lu.id
                FROM learning_units lu
                JOIN unit_tree ut ON lu.parent_id = ut.id
            ),
            
            unit_exercises AS (
                SELECT e.id AS exercise_id
                FROM exercises e
                JOIN unit_tree ut ON e.learning_unit_id = ut.id
            ),
            
            best_attempts AS (
                SELECT
                    ad.exercise_id,
                    MAX(ad.score) AS best_score
                FROM attempts a
                JOIN attempt_details ad ON ad.attempt_id = a.id
                WHERE a.user_id = :userId
                GROUP BY ad.exercise_id
            )
            
            SELECT
                COUNT(CASE WHEN ba.best_score >= 50 THEN 1 END)
            FROM unit_exercises ue
            LEFT JOIN best_attempts ba
                   ON ba.exercise_id = ue.exercise_id
            """, nativeQuery = true)
    long getExerciseStatistic(UUID learningUnitId, UUID userId);
}
