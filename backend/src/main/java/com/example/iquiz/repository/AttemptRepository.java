package com.example.iquiz.repository;

import com.example.iquiz.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, UUID> {
    List<Attempt> findByUserId(UUID userId);

    @Query(value = """
            WITH RECURSIVE unit_tree AS (
                SELECT id 
                FROM learning_units 
                WHERE id = :learningUnitId
            
                UNION ALL
            
                SELECT lu.id 
                FROM learning_units lu
                INNER JOIN unit_tree ut ON lu.parent_id = ut.id
            )
            SELECT a.* FROM attempts a
            """, nativeQuery = true)
    List<Attempt> findByLearningUnitId(@Param("learningUnitId") UUID learningUnitId);

    List<Attempt> findByUserIdAndLessonId(UUID userId, UUID lessonId);
}
