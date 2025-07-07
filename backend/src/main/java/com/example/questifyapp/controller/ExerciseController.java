package com.example.questifyapp.controller;

import com.example.questifyapp.dto.ExerciseDTO;
import com.example.questifyapp.dto.OptionDTO;
import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.entity.Option;
import com.example.questifyapp.mapper.ExerciseMapper;
import com.example.questifyapp.mapper.OptionMapper;
import com.example.questifyapp.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
    @Autowired
    private ExerciseService exerciseService;

    // Existing endpoints (keeping them as-is)
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDTO> getExercise(@PathVariable Long id) {
        Exercise exercise = exerciseService.getExerciseById(id);
        if (exercise == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ExerciseMapper.toDto(exercise));
    }

    @GetMapping("/{exerciseId}/options")
    public ResponseEntity<List<OptionDTO>> getOptionsForExercise(@PathVariable Long exerciseId) {
        List<Option> options = exerciseService.getOptionsByExerciseId(exerciseId);
        if (options == null) {
            return ResponseEntity.notFound().build();
        }
        List<OptionDTO> optionDTOs = options.stream()
                .map(OptionMapper::toDto)
                .toList();
        return ResponseEntity.ok(optionDTOs);
    }

    // New CRUD endpoints
    
    /**
     * Get all exercises
     */
    @GetMapping("")
    public ResponseEntity<List<ExerciseDTO>> getAllExercises() {
        List<Exercise> exercises = exerciseService.getAllExercises();
        return ResponseEntity.ok(exercises.stream()
                .map(ExerciseMapper::toDto)
                .toList());
    }

    /**
     * Create a new exercise
     */
    @PostMapping("")
    public ResponseEntity<ExerciseDTO> createExercise(@RequestBody ExerciseDTO exerciseDTO) {
        try {
            Exercise exercise = ExerciseMapper.toEntity(exerciseDTO);
            Exercise createdExercise = exerciseService.createExercise(exercise);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ExerciseMapper.toDto(createdExercise));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Update an existing exercise
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExerciseDTO> updateExercise(
            @PathVariable Long id, 
            @RequestBody ExerciseDTO exerciseDTO) {
        
        if (!exerciseService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Exercise exercise = ExerciseMapper.toEntity(exerciseDTO);
            exercise.setId(id);
            Exercise updatedExercise = exerciseService.updateExercise(exercise);
            return ResponseEntity.ok(ExerciseMapper.toDto(updatedExercise));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Delete exercise by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteExercise(@PathVariable Long id) {
        if (!exerciseService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            exerciseService.deleteExercise(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Exercise deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete exercise");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Search and filter endpoints
    
    /**
     * Get exercises by parent unit ID
     */
    @GetMapping("/learning-unit/{parentUnitId}")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByParentUnitId(@PathVariable Long parentUnitId) {
        List<Exercise> exercises = exerciseService.getExercisesByParentUnitId(parentUnitId);
        return ResponseEntity.ok(exercises.stream()
                .map(ExerciseMapper::toDto)
                .toList());
    }

    /**
     * Get exercises by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByType(@PathVariable String type) {
        List<Exercise> exercises = exerciseService.getExercisesByType(type);
        return ResponseEntity.ok(exercises.stream()
                .map(ExerciseMapper::toDto)
                .toList());
    }

    /**
     * Search exercises by question text
     */
    @GetMapping("/search/question")
    public ResponseEntity<List<ExerciseDTO>> searchExercisesByQuestion(@RequestParam String question) {
        List<Exercise> exercises = exerciseService.searchExercisesByQuestion(question);
        if (exercises.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(exercises.stream()
                .map(ExerciseMapper::toDto)
                .toList());
    }

    /**
     * Search exercises by question or answer
     */
    @GetMapping("/search")
    public ResponseEntity<List<ExerciseDTO>> searchExercises(@RequestParam String searchTerm) {
        List<Exercise> exercises = exerciseService.searchExercisesByQuestionOrAnswer(searchTerm);
        if (exercises.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(exercises.stream()
                .map(ExerciseMapper::toDto)
                .toList());
    }

    /**
     * Get exercises by type and parent unit ID
     */
    @GetMapping("/filter")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByTypeAndParentUnit(
            @RequestParam String type, 
            @RequestParam Long parentUnitId) {
        
        List<Exercise> exercises = exerciseService.getExercisesByTypeAndParentUnitId(type, parentUnitId);
        return ResponseEntity.ok(exercises.stream()
                .map(ExerciseMapper::toDto)
                .toList());
    }

    /**
     * Get exercises created after a certain date
     */
    @GetMapping("/created-after")
    public ResponseEntity<List<ExerciseDTO>> getExercisesCreatedAfter(@RequestParam String date) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date);
            List<Exercise> exercises = exerciseService.getExercisesCreatedAfter(dateTime);
            return ResponseEntity.ok(exercises.stream()
                    .map(ExerciseMapper::toDto)
                    .toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get exercises created between dates
     */
    @GetMapping("/created-between")
    public ResponseEntity<List<ExerciseDTO>> getExercisesCreatedBetween(
            @RequestParam String startDate, 
            @RequestParam String endDate) {
        
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startDate);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate);
            List<Exercise> exercises = exerciseService.getExercisesCreatedBetween(startDateTime, endDateTime);
            return ResponseEntity.ok(exercises.stream()
                    .map(ExerciseMapper::toDto)
                    .toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Ordering endpoints
    
    /**
     * Get all exercises ordered by creation date (newest first)
     */
    @GetMapping("/ordered-by-created")
    public ResponseEntity<List<ExerciseDTO>> getAllExercisesOrderedByCreatedDate() {
        List<Exercise> exercises = exerciseService.getAllExercisesOrderedByCreatedDate();
        return ResponseEntity.ok(exercises.stream()
                .map(ExerciseMapper::toDto)
                .toList());
    }

    /**
     * Get all exercises ordered by update date (newest first)
     */
    @GetMapping("/ordered-by-updated")
    public ResponseEntity<List<ExerciseDTO>> getAllExercisesOrderedByUpdatedDate() {
        List<Exercise> exercises = exerciseService.getAllExercisesOrderedByUpdatedDate();
        return ResponseEntity.ok(exercises.stream()
                .map(ExerciseMapper::toDto)
                .toList());
    }

    /**
     * Get exercises by parent unit ID ordered by creation date
     */
    @GetMapping("/learning-unit/{parentUnitId}/ordered-by-created")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByParentUnitOrderedByCreatedDate(
            @PathVariable Long parentUnitId) {
        
        List<Exercise> exercises = exerciseService.getExercisesByParentUnitIdOrderedByCreatedDate(parentUnitId);
        return ResponseEntity.ok(exercises.stream()
                .map(ExerciseMapper::toDto)
                .toList());
    }

    // Statistics endpoints
    
    /**
     * Get total exercises count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalExercisesCount() {
        long count = exerciseService.countExercises();
        Map<String, Long> response = new HashMap<>();
        response.put("total", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get exercises count by parent unit ID
     */
    @GetMapping("/count/learning-unit/{parentUnitId}")
    public ResponseEntity<Map<String, Long>> getExercisesCountByParentUnit(@PathVariable Long parentUnitId) {
        long count = exerciseService.countExercisesByParentUnitId(parentUnitId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        response.put("parentUnitId", parentUnitId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get exercises count by type
     */
    @GetMapping("/count/type/{type}")
    public ResponseEntity<Map<String, Long>> getExercisesCountByType(@PathVariable String type) {
        long count = exerciseService.countExercisesByType(type);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // Utility endpoints
    
    /**
     * Check if exercise exists by question
     */
    @GetMapping("/exists/question/{question}")
    public ResponseEntity<Map<String, Boolean>> existsByQuestion(@PathVariable String question) {
        boolean exists = exerciseService.existsByQuestion(question);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    /**
     * Get exercises with options
     */
    @GetMapping("/with-options")
    public ResponseEntity<List<ExerciseDTO>> getExercisesWithOptions() {
        List<Exercise> exercises = exerciseService.getExercisesWithOptionsCount();
        return ResponseEntity.ok(exercises.stream()
                .map(ExerciseMapper::toDto)
                .toList());
    }

    /**
     * Get exercises without options
     */
    @GetMapping("/without-options")
    public ResponseEntity<List<ExerciseDTO>> getExercisesWithoutOptions() {
        List<Exercise> exercises = exerciseService.getExercisesWithoutOptions();
        return ResponseEntity.ok(exercises.stream()
                .map(ExerciseMapper::toDto)
                .toList());
    }
}
