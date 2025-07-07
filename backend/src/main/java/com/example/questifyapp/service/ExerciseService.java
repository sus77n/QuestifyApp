package com.example.questifyapp.service;

import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.entity.LearningUnit;
import com.example.questifyapp.entity.Option;
import com.example.questifyapp.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {
    @Autowired
    private ExerciseRepository exerciseRepository;

    // Existing methods (keeping them as-is)
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public Exercise getExerciseById(Long exerciseId) {
        return exerciseRepository.findById(exerciseId).orElse(null);
    }

    public List<Option> getOptionsByExerciseId(Long exerciseId) {
        Exercise exercise = getExerciseById(exerciseId);
        return exercise != null ? exercise.getOptions() : null;
    }

    // New CRUD methods

    /**
     * Get exercise by ID with Optional return
     */
    public Optional<Exercise> findExerciseById(Long id) {
        return exerciseRepository.findById(id);
    }

    /**
     * Create a new exercise
     */
    public Exercise createExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    /**
     * Update an existing exercise
     */
    public Exercise updateExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    /**
     * Delete exercise by ID
     */
    public void deleteExercise(Long id) {
        exerciseRepository.deleteById(id);
    }

    /**
     * Check if exercise exists by ID
     */
    public boolean existsById(Long id) {
        return exerciseRepository.existsById(id);
    }

    /**
     * Check if exercise exists by question
     */
    public boolean existsByQuestion(String question) {
        return exerciseRepository.existsByQuestion(question);
    }

    // Search and filter methods

    /**
     * Get exercises by parent unit
     */
    public List<Exercise> getExercisesByParentUnit(LearningUnit parentUnit) {
        return exerciseRepository.findByParentUnit(parentUnit);
    }

    /**
     * Get exercises by parent unit ID
     */
    public List<Exercise> getExercisesByParentUnitId(Long parentUnitId) {
        return exerciseRepository.findByParentUnitId(parentUnitId);
    }

    /**
     * Get exercises by type
     */
    public List<Exercise> getExercisesByType(String type) {
        return exerciseRepository.findByType(type);
    }

    /**
     * Search exercises by question text
     */
    public List<Exercise> searchExercisesByQuestion(String question) {
        return exerciseRepository.findByQuestionContainingIgnoreCase(question);
    }

    /**
     * Search exercises by question or answer
     */
    public List<Exercise> searchExercisesByQuestionOrAnswer(String searchTerm) {
        return exerciseRepository.searchByQuestionOrAnswer(searchTerm);
    }

    /**
     * Get exercises created after a certain date
     */
    public List<Exercise> getExercisesCreatedAfter(LocalDateTime date) {
        return exerciseRepository.findByCreatedAtAfter(date);
    }

    /**
     * Get exercises created before a certain date
     */
    public List<Exercise> getExercisesCreatedBefore(LocalDateTime date) {
        return exerciseRepository.findByCreatedAtBefore(date);
    }

    /**
     * Get exercises created between dates
     */
    public List<Exercise> getExercisesCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return exerciseRepository.findByCreatedAtBetween(startDate, endDate);
    }

    /**
     * Get exercises by type and parent unit
     */
    public List<Exercise> getExercisesByTypeAndParentUnit(String type, LearningUnit parentUnit) {
        return exerciseRepository.findByTypeAndParentUnit(type, parentUnit);
    }

    /**
     * Get exercises by type and parent unit ID
     */
    public List<Exercise> getExercisesByTypeAndParentUnitId(String type, Long parentUnitId) {
        return exerciseRepository.findByTypeAndParentUnitId(type, parentUnitId);
    }

    // Ordering methods

    /**
     * Get all exercises ordered by creation date (newest first)
     */
    public List<Exercise> getAllExercisesOrderedByCreatedDate() {
        return exerciseRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get all exercises ordered by update date (newest first)
     */
    public List<Exercise> getAllExercisesOrderedByUpdatedDate() {
        return exerciseRepository.findAllByOrderByUpdatedAtDesc();
    }

    /**
     * Get exercises by parent unit ordered by creation date
     */
    public List<Exercise> getExercisesByParentUnitOrderedByCreatedDate(LearningUnit parentUnit) {
        return exerciseRepository.findByParentUnitOrderByCreatedAtDesc(parentUnit);
    }

    /**
     * Get exercises by parent unit ID ordered by creation date
     */
    public List<Exercise> getExercisesByParentUnitIdOrderedByCreatedDate(Long parentUnitId) {
        return exerciseRepository.findByParentUnitIdOrderByCreatedAtDesc(parentUnitId);
    }

    // Count methods

    /**
     * Count total exercises
     */
    public long countExercises() {
        return exerciseRepository.count();
    }

    /**
     * Count exercises by parent unit
     */
    public long countExercisesByParentUnit(LearningUnit parentUnit) {
        return exerciseRepository.countByParentUnit(parentUnit);
    }

    /**
     * Count exercises by parent unit ID
     */
    public long countExercisesByParentUnitId(Long parentUnitId) {
        return exerciseRepository.countByParentUnitId(parentUnitId);
    }

    /**
     * Count exercises by type
     */
    public long countExercisesByType(String type) {
        return exerciseRepository.countByType(type);
    }

    // Business logic methods

    /**
     * Add an option to an exercise
     */
    public Exercise addOptionToExercise(Long exerciseId, Option option) {
        Exercise exercise = getExerciseById(exerciseId);
        if (exercise != null) {
            exercise.getOptions().add(option);
            option.setExercise(exercise);
            return exerciseRepository.save(exercise);
        }
        return null;
    }

    /**
     * Remove an option from an exercise
     */
    public Exercise removeOptionFromExercise(Long exerciseId, Long optionId) {
        Exercise exercise = getExerciseById(exerciseId);
        if (exercise != null) {
            exercise.getOptions().removeIf(option -> option.getId().equals(optionId));
            return exerciseRepository.save(exercise);
        }
        return null;
    }

    /**
     * Get exercises with options count
     */
    public List<Exercise> getExercisesWithOptionsCount() {
        return exerciseRepository.findAll().stream()
                .filter(exercise -> !exercise.getOptions().isEmpty())
                .toList();
    }

    /**
     * Get exercises without options
     */
    public List<Exercise> getExercisesWithoutOptions() {
        return exerciseRepository.findAll().stream()
                .filter(exercise -> exercise.getOptions().isEmpty())
                .toList();
    }
}
