package com.example.iquiz.repository;

import com.example.iquiz.entity.Exercise;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    @Override
    Optional<Exercise> findById(Long id);

    @Override
    <S extends Exercise> List<S> findAll(Example<S> example);

    @Query("""
                SELECT e FROM Exercise e
                WHERE e.parent.id = :unitId
                AND e.id NOT IN (
                    SELECT s.exercise.id FROM Submission s WHERE s.user.id = :userId
                )
            """)
    List<Exercise> findUnsubmittedExercisesByUserIdAndUnitId(
            @Param("unitId") Long unitId,
            @Param("userId") Long userId
    );

    List<Exercise> findByParent_Id(Long parentId);

    List<Exercise> findByParent_IdIn(List<Long> parentIds);
}

