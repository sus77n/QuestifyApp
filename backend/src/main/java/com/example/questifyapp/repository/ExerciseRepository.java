package com.example.questifyapp.repository;

import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.entity.LearningUnit;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    // Existing methods (keeping them as-is)
    @Override
    Optional<Exercise> findById(Long id);

    @Override
    <S extends Exercise> List<S> findAll(Example<S> example);

    // New CRUD and query methods

    // Find exercises by parent unit
    List<Exercise> findByParentUnit(LearningUnit parentUnit);

    // Find exercises by parent unit ID
    List<Exercise> findByParentUnitId(Long parentUnitId);

    // Find exercises by type
    List<Exercise> findByType(String type);

    // Find exercises by question containing text (search)
    List<Exercise> findByQuestionContainingIgnoreCase(String question);

    // Find exercises created after a certain date
    List<Exercise> findByCreatedAtAfter(LocalDateTime date);

    // Find exercises created before a certain date
    List<Exercise> findByCreatedAtBefore(LocalDateTime date);

    // Find exercises created between dates
    List<Exercise> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find exercises by type and parent unit
    List<Exercise> findByTypeAndParentUnit(String type, LearningUnit parentUnit);

    // Find exercises by type and parent unit ID
    List<Exercise> findByTypeAndParentUnitId(String type, Long parentUnitId);

    // Count exercises by parent unit
    long countByParentUnit(LearningUnit parentUnit);

    // Count exercises by parent unit ID
    long countByParentUnitId(Long parentUnitId);

    // Count exercises by type
    long countByType(String type);

    // Check if exercise exists by question
    boolean existsByQuestion(String question);

    // Custom query to search exercises by question or answer
    @Query("SELECT e FROM Exercise e WHERE LOWER(e.question) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.answer) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Exercise> searchByQuestionOrAnswer(@Param("searchTerm") String searchTerm);

    // Find exercises ordered by creation date
    List<Exercise> findAllByOrderByCreatedAtDesc();

    // Find exercises ordered by update date
    List<Exercise> findAllByOrderByUpdatedAtDesc();

    // Find exercises by parent unit ordered by creation date
    List<Exercise> findByParentUnitOrderByCreatedAtDesc(LearningUnit parentUnit);

    // Find exercises by parent unit ID ordered by creation date
    List<Exercise> findByParentUnitIdOrderByCreatedAtDesc(Long parentUnitId);
}
