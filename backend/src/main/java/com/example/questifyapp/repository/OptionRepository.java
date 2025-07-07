package com.example.questifyapp.repository;

import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {
    
    // Existing method (keeping it as-is)
    List<Option> findByExerciseId(Long exerciseId);
    
    // New CRUD and query methods
    
    // Find options by exercise
    List<Option> findByExercise(Exercise exercise);
    
    // Find options by text containing (search)
    List<Option> findByTextContainingIgnoreCase(String text);
    
    // Find correct options
    List<Option> findByIsCorrect(boolean isCorrect);
    
    // Find correct options by exercise
    List<Option> findByExerciseAndIsCorrect(Exercise exercise, boolean isCorrect);
    
    // Find correct options by exercise ID
    List<Option> findByExerciseIdAndIsCorrect(Long exerciseId, boolean isCorrect);
    
    // Count options by exercise
    long countByExercise(Exercise exercise);
    
    // Count options by exercise ID
    long countByExerciseId(Long exerciseId);
    
    // Count correct options by exercise
    long countByExerciseAndIsCorrect(Exercise exercise, boolean isCorrect);
    
    // Count correct options by exercise ID
    long countByExerciseIdAndIsCorrect(Long exerciseId, boolean isCorrect);
    
    // Find options by exercise ordered by text
    List<Option> findByExerciseOrderByTextAsc(Exercise exercise);
    
    // Find options by exercise ID ordered by text
    List<Option> findByExerciseIdOrderByTextAsc(Long exerciseId);
    
    // Check if option exists by text and exercise
    boolean existsByTextAndExercise(String text, Exercise exercise);
    
    // Check if option exists by text and exercise ID
    boolean existsByTextAndExerciseId(String text, Long exerciseId);
    
    // Custom query to search options by text
    @Query("SELECT o FROM Option o WHERE LOWER(o.text) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Option> searchByText(@Param("searchTerm") String searchTerm);
    
    // Find all correct options
    @Query("SELECT o FROM Option o WHERE o.isCorrect = true")
    List<Option> findAllCorrectOptions();
    
    // Find all incorrect options
    @Query("SELECT o FROM Option o WHERE o.isCorrect = false")
    List<Option> findAllIncorrectOptions();
    
    // Count total correct options
    @Query("SELECT COUNT(o) FROM Option o WHERE o.isCorrect = true")
    long countCorrectOptions();
    
    // Count total incorrect options
    @Query("SELECT COUNT(o) FROM Option o WHERE o.isCorrect = false")
    long countIncorrectOptions();
    
    // Find options by exercise type
    @Query("SELECT o FROM Option o JOIN o.exercise e WHERE e.type = :exerciseType")
    List<Option> findByExerciseType(@Param("exerciseType") String exerciseType);
    
    // Find correct options by exercise type
    @Query("SELECT o FROM Option o JOIN o.exercise e WHERE e.type = :exerciseType AND o.isCorrect = true")
    List<Option> findCorrectOptionsByExerciseType(@Param("exerciseType") String exerciseType);
}
