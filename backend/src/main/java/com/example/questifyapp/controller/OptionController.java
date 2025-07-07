package com.example.questifyapp.controller;

import com.example.questifyapp.dto.OptionDTO;
import com.example.questifyapp.entity.Option;
import com.example.questifyapp.mapper.OptionMapper;
import com.example.questifyapp.repository.OptionRepository;
import com.example.questifyapp.service.OptionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/options")
public class OptionController {
    @Autowired
    private OptionService optionService;
    @Autowired
    private OptionRepository optionRepository;

    // Existing endpoints (keeping them as-is)
    @GetMapping("")
    public ResponseEntity<List<OptionDTO>> getAllOptions() {
        List<Option> options = optionService.getAllOptions();
        return ResponseEntity.ok(options.stream()
                .map(OptionMapper::toDto)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptionDTO> getOption(@PathVariable Long id) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Option not found"));
        return ResponseEntity.ok(OptionMapper.toDto(option));
    }

    // New CRUD endpoints

    /**
     * Create a new option
     */
    @PostMapping("")
    public ResponseEntity<OptionDTO> createOption(@RequestBody OptionDTO optionDTO) {
        try {
            Option option = OptionMapper.toEntity(optionDTO);
            Option createdOption = optionService.createOption(option);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(OptionMapper.toDto(createdOption));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Update an existing option
     */
    @PutMapping("/{id}")
    public ResponseEntity<OptionDTO> updateOption(
            @PathVariable Long id,
            @RequestBody OptionDTO optionDTO) {

        if (!optionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            Option option = OptionMapper.toEntity(optionDTO);
            option.setId(id);
            Option updatedOption = optionService.updateOptionAndReturn(option);
            return ResponseEntity.ok(OptionMapper.toDto(updatedOption));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Delete option by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOption(@PathVariable Long id) {
        if (!optionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            optionService.deleteOption(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Option deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete option");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Search and filter endpoints

    /**
     * Get options by exercise ID
     */
    @GetMapping("/exercise/{exerciseId}")
    public ResponseEntity<List<OptionDTO>> getOptionsByExerciseId(@PathVariable Long exerciseId) {
        List<Option> options = optionService.getOptionsByExerciseId(exerciseId);
        return ResponseEntity.ok(options.stream()
                .map(OptionMapper::toDto)
                .toList());
    }

    /**
     * Get options by exercise ID ordered by text
     */
    @GetMapping("/exercise/{exerciseId}/ordered")
    public ResponseEntity<List<OptionDTO>> getOptionsByExerciseIdOrderedByText(@PathVariable Long exerciseId) {
        List<Option> options = optionService.getOptionsByExerciseIdOrderedByText(exerciseId);
        return ResponseEntity.ok(options.stream()
                .map(OptionMapper::toDto)
                .toList());
    }

    /**
     * Search options by text
     */
    @GetMapping("/search")
    public ResponseEntity<List<OptionDTO>> searchOptionsByText(@RequestParam String text) {
        List<Option> options = optionService.searchOptionsByText(text);
        if (options.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(options.stream()
                .map(OptionMapper::toDto)
                .toList());
    }

    /**
     * Get options by correctness
     */
    @GetMapping("/correct/{isCorrect}")
    public ResponseEntity<List<OptionDTO>> getOptionsByCorrectness(@PathVariable boolean isCorrect) {
        List<Option> options = optionService.getOptionsByCorrectness(isCorrect);
        return ResponseEntity.ok(options.stream()
                .map(OptionMapper::toDto)
                .toList());
    }

    /**
     * Get all correct options
     */
    @GetMapping("/correct")
    public ResponseEntity<List<OptionDTO>> getAllCorrectOptions() {
        List<Option> options = optionService.getAllCorrectOptions();
        return ResponseEntity.ok(options.stream()
                .map(OptionMapper::toDto)
                .toList());
    }

    /**
     * Get all incorrect options
     */
    @GetMapping("/incorrect")
    public ResponseEntity<List<OptionDTO>> getAllIncorrectOptions() {
        List<Option> options = optionService.getAllIncorrectOptions();
        return ResponseEntity.ok(options.stream()
                .map(OptionMapper::toDto)
                .toList());
    }

    /**
     * Get correct options by exercise ID
     */
    @GetMapping("/exercise/{exerciseId}/correct")
    public ResponseEntity<List<OptionDTO>> getCorrectOptionsByExerciseId(@PathVariable Long exerciseId) {
        List<Option> options = optionService.getCorrectOptionsByExerciseId(exerciseId);
        return ResponseEntity.ok(options.stream()
                .map(OptionMapper::toDto)
                .toList());
    }

    /**
     * Get incorrect options by exercise ID
     */
    @GetMapping("/exercise/{exerciseId}/incorrect")
    public ResponseEntity<List<OptionDTO>> getIncorrectOptionsByExerciseId(@PathVariable Long exerciseId) {
        List<Option> options = optionService.getIncorrectOptionsByExerciseId(exerciseId);
        return ResponseEntity.ok(options.stream()
                .map(OptionMapper::toDto)
                .toList());
    }

    /**
     * Get options by exercise type
     */
    @GetMapping("/exercise-type/{exerciseType}")
    public ResponseEntity<List<OptionDTO>> getOptionsByExerciseType(@PathVariable String exerciseType) {
        List<Option> options = optionService.getOptionsByExerciseType(exerciseType);
        return ResponseEntity.ok(options.stream()
                .map(OptionMapper::toDto)
                .toList());
    }

    /**
     * Get correct options by exercise type
     */
    @GetMapping("/exercise-type/{exerciseType}/correct")
    public ResponseEntity<List<OptionDTO>> getCorrectOptionsByExerciseType(@PathVariable String exerciseType) {
        List<Option> options = optionService.getCorrectOptionsByExerciseType(exerciseType);
        return ResponseEntity.ok(options.stream()
                .map(OptionMapper::toDto)
                .toList());
    }

    // Business logic endpoints

    /**
     * Mark option as correct
     */
    @PutMapping("/{id}/mark-correct")
    public ResponseEntity<OptionDTO> markAsCorrect(@PathVariable Long id) {
        if (!optionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            Option updatedOption = optionService.markAsCorrect(id);
            if (updatedOption != null) {
                return ResponseEntity.ok(OptionMapper.toDto(updatedOption));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Mark option as incorrect
     */
    @PutMapping("/{id}/mark-incorrect")
    public ResponseEntity<OptionDTO> markAsIncorrect(@PathVariable Long id) {
        if (!optionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            Option updatedOption = optionService.markAsIncorrect(id);
            if (updatedOption != null) {
                return ResponseEntity.ok(OptionMapper.toDto(updatedOption));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Toggle option correctness
     */
    @PutMapping("/{id}/toggle-correctness")
    public ResponseEntity<OptionDTO> toggleCorrectness(@PathVariable Long id) {
        if (!optionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            Option updatedOption = optionService.toggleCorrectness(id);
            if (updatedOption != null) {
                return ResponseEntity.ok(OptionMapper.toDto(updatedOption));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Statistics endpoints

    /**
     * Get total options count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalOptionsCount() {
        long count = optionService.totalOptions();
        Map<String, Long> response = new HashMap<>();
        response.put("total", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get options count by exercise ID
     */
    @GetMapping("/count/exercise/{exerciseId}")
    public ResponseEntity<Map<String, Long>> getOptionsCountByExerciseId(@PathVariable Long exerciseId) {
        long count = optionService.countOptionsByExerciseId(exerciseId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        response.put("exerciseId", exerciseId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get correct options count by exercise ID
     */
    @GetMapping("/count/exercise/{exerciseId}/correct")
    public ResponseEntity<Map<String, Long>> getCorrectOptionsCountByExerciseId(@PathVariable Long exerciseId) {
        long count = optionService.countCorrectOptionsByExerciseId(exerciseId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        response.put("exerciseId", exerciseId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get incorrect options count by exercise ID
     */
    @GetMapping("/count/exercise/{exerciseId}/incorrect")
    public ResponseEntity<Map<String, Long>> getIncorrectOptionsCountByExerciseId(@PathVariable Long exerciseId) {
        long count = optionService.countIncorrectOptionsByExerciseId(exerciseId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        response.put("exerciseId", exerciseId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get total correct options count
     */
    @GetMapping("/count/correct")
    public ResponseEntity<Map<String, Long>> getTotalCorrectOptionsCount() {
        long count = optionService.countTotalCorrectOptions();
        Map<String, Long> response = new HashMap<>();
        response.put("total", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get total incorrect options count
     */
    @GetMapping("/count/incorrect")
    public ResponseEntity<Map<String, Long>> getTotalIncorrectOptionsCount() {
        long count = optionService.countTotalIncorrectOptions();
        Map<String, Long> response = new HashMap<>();
        response.put("total", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get option statistics for exercise
     */
    @GetMapping("/stats/exercise/{exerciseId}")
    public ResponseEntity<Map<String, Object>> getOptionStatsForExercise(@PathVariable Long exerciseId) {
        OptionService.OptionStats stats = optionService.getOptionStatsForExercise(exerciseId);
        Map<String, Object> response = new HashMap<>();
        response.put("exerciseId", exerciseId);
        response.put("totalOptions", stats.getTotal());
        response.put("correctOptions", stats.getCorrect());
        response.put("incorrectOptions", stats.getIncorrect());
        return ResponseEntity.ok(response);
    }

    // Validation endpoints

    /**
     * Check if option exists by text and exercise ID
     */
    @GetMapping("/exists/text/{text}/exercise/{exerciseId}")
    public ResponseEntity<Map<String, Boolean>> existsByTextAndExerciseId(
            @PathVariable String text,
            @PathVariable Long exerciseId) {

        boolean exists = optionService.existsByTextAndExerciseId(text, exerciseId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    /**
     * Check if exercise has correct answer
     */
    @GetMapping("/exercise/{exerciseId}/has-correct-answer")
    public ResponseEntity<Map<String, Boolean>> exerciseHasCorrectAnswer(@PathVariable Long exerciseId) {
        boolean hasCorrectAnswer = optionService.exerciseHasCorrectAnswer(exerciseId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasCorrectAnswer", hasCorrectAnswer);
        return ResponseEntity.ok(response);
    }
}
