package com.example.iquiz.repository;

import com.example.iquiz.entity.Exercise;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {

    List<Exercise> findByParent_Id(UUID parentId);

    List<Exercise> findByParent_IdIn(List<UUID> parentIds);

    @Query("""
                SELECT e FROM Exercise e
                LEFT JOIN FETCH e.predefinedAnswers
                WHERE e.parent.id = :id
            """)
    List<Exercise> findExercisesWithAnswers(UUID id);


    @Query(value = """
            WITH UnitTree AS (
                SELECT id, parent_id
                FROM learning_unit
                WHERE id = :learningUnitId
                UNION ALL
                SELECT lu.id, lu.parent_id
                FROM learning_unit lu
                JOIN UnitTree t ON lu.parent_id = t.id
            )
            SELECT COUNT(*)
            FROM exercise e
            JOIN UnitTree t ON e.learning_unit_id = t.id
            """, nativeQuery = true)
    int countExercisesInLearningUnitTree(UUID learningUnitId);

}

