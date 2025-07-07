package com.example.questifyapp.service;

import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.entity.Option;
import com.example.questifyapp.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OptionService {
    @Autowired
    private OptionRepository optionRepository;

    // Existing methods (keeping them as-is)
    public List<Option> getAllOptions() {
        return optionRepository.findAll();
    }

    public Option getOptionById(Long id) {
        return optionRepository.findById(id).orElse(null);
    }

    public void addOption(Option option) {
        optionRepository.save(option);
    }

    public void updateOption(Option option) {
        optionRepository.save(option);
    }

    public void deleteOptionById(Long id) {
        optionRepository.deleteById(id);
    }

    public Long totalOptions() {
        return optionRepository.count();
    }

    // New CRUD methods

    /**
     * Get option by ID with Optional return
     */
    public Optional<Option> findOptionById(Long id) {
        return optionRepository.findById(id);
    }

    /**
     * Create a new option
     */
    public Option createOption(Option option) {
        return optionRepository.save(option);
    }

    /**
     * Update an existing option (return updated option)
     */
    public Option updateOptionAndReturn(Option option) {
        return optionRepository.save(option);
    }

    /**
     * Delete option by ID
     */
    public void deleteOption(Long id) {
        optionRepository.deleteById(id);
    }

    /**
     * Check if option exists by ID
     */
    public boolean existsById(Long id) {
        return optionRepository.existsById(id);
    }

    // Search and filter methods

    /**
     * Get options by exercise ID
     */
    public List<Option> getOptionsByExerciseId(Long exerciseId) {
        return optionRepository.findByExerciseId(exerciseId);
    }

    /**
     * Get options by exercise
     */
    public List<Option> getOptionsByExercise(Exercise exercise) {
        return optionRepository.findByExercise(exercise);
    }

    /**
     * Search options by text
     */
    public List<Option> searchOptionsByText(String text) {
        return optionRepository.findByTextContainingIgnoreCase(text);
    }

    /**
     * Search options by text (custom query)
     */
    public List<Option> searchByText(String searchTerm) {
        return optionRepository.searchByText(searchTerm);
    }

    /**
     * Get options by correctness
     */
    public List<Option> getOptionsByCorrectness(boolean isCorrect) {
        return optionRepository.findByIsCorrect(isCorrect);
    }

    /**
     * Get all correct options
     */
    public List<Option> getAllCorrectOptions() {
        return optionRepository.findAllCorrectOptions();
    }

    /**
     * Get all incorrect options
     */
    public List<Option> getAllIncorrectOptions() {
        return optionRepository.findAllIncorrectOptions();
    }

    /**
     * Get correct options by exercise
     */
    public List<Option> getCorrectOptionsByExercise(Exercise exercise) {
        return optionRepository.findByExerciseAndIsCorrect(exercise, true);
    }

    /**
     * Get correct options by exercise ID
     */
    public List<Option> getCorrectOptionsByExerciseId(Long exerciseId) {
        return optionRepository.findByExerciseIdAndIsCorrect(exerciseId, true);
    }

    /**
     * Get incorrect options by exercise
     */
    public List<Option> getIncorrectOptionsByExercise(Exercise exercise) {
        return optionRepository.findByExerciseAndIsCorrect(exercise, false);
    }

    /**
     * Get incorrect options by exercise ID
     */
    public List<Option> getIncorrectOptionsByExerciseId(Long exerciseId) {
        return optionRepository.findByExerciseIdAndIsCorrect(exerciseId, false);
    }

    /**
     * Get options by exercise type
     */
    public List<Option> getOptionsByExerciseType(String exerciseType) {
        return optionRepository.findByExerciseType(exerciseType);
    }

    /**
     * Get correct options by exercise type
     */
    public List<Option> getCorrectOptionsByExerciseType(String exerciseType) {
        return optionRepository.findCorrectOptionsByExerciseType(exerciseType);
    }

    // Ordering methods

    /**
     * Get options by exercise ordered by text
     */
    public List<Option> getOptionsByExerciseOrderedByText(Exercise exercise) {
        return optionRepository.findByExerciseOrderByTextAsc(exercise);
    }

    /**
     * Get options by exercise ID ordered by text
     */
    public List<Option> getOptionsByExerciseIdOrderedByText(Long exerciseId) {
        return optionRepository.findByExerciseIdOrderByTextAsc(exerciseId);
    }

    // Count methods

    /**
     * Count options by exercise
     */
    public long countOptionsByExercise(Exercise exercise) {
        return optionRepository.countByExercise(exercise);
    }

    /**
     * Count options by exercise ID
     */
    public long countOptionsByExerciseId(Long exerciseId) {
        return optionRepository.countByExerciseId(exerciseId);
    }

    /**
     * Count correct options by exercise
     */
    public long countCorrectOptionsByExercise(Exercise exercise) {
        return optionRepository.countByExerciseAndIsCorrect(exercise, true);
    }

    /**
     * Count correct options by exercise ID
     */
    public long countCorrectOptionsByExerciseId(Long exerciseId) {
        return optionRepository.countByExerciseIdAndIsCorrect(exerciseId, true);
    }

    /**
     * Count incorrect options by exercise
     */
    public long countIncorrectOptionsByExercise(Exercise exercise) {
        return optionRepository.countByExerciseAndIsCorrect(exercise, false);
    }

    /**
     * Count incorrect options by exercise ID
     */
    public long countIncorrectOptionsByExerciseId(Long exerciseId) {
        return optionRepository.countByExerciseIdAndIsCorrect(exerciseId, false);
    }

    /**
     * Count total correct options
     */
    public long countTotalCorrectOptions() {
        return optionRepository.countCorrectOptions();
    }

    /**
     * Count total incorrect options
     */
    public long countTotalIncorrectOptions() {
        return optionRepository.countIncorrectOptions();
    }

    // Validation methods

    /**
     * Check if option exists by text and exercise
     */
    public boolean existsByTextAndExercise(String text, Exercise exercise) {
        return optionRepository.existsByTextAndExercise(text, exercise);
    }

    /**
     * Check if option exists by text and exercise ID
     */
    public boolean existsByTextAndExerciseId(String text, Long exerciseId) {
        return optionRepository.existsByTextAndExerciseId(text, exerciseId);
    }

    // Business logic methods

    /**
     * Mark option as correct
     */
    public Option markAsCorrect(Long optionId) {
        Option option = getOptionById(optionId);
        if (option != null) {
            option.setCorrect(true);
            return optionRepository.save(option);
        }
        return null;
    }

    /**
     * Mark option as incorrect
     */
    public Option markAsIncorrect(Long optionId) {
        Option option = getOptionById(optionId);
        if (option != null) {
            option.setCorrect(false);
            return optionRepository.save(option);
        }
        return null;
    }

    /**
     * Toggle option correctness
     */
    public Option toggleCorrectness(Long optionId) {
        Option option = getOptionById(optionId);
        if (option != null) {
            option.setCorrect(!option.isCorrect());
            return optionRepository.save(option);
        }
        return null;
    }

    /**
     * Check if exercise has correct answer
     */
    public boolean exerciseHasCorrectAnswer(Long exerciseId) {
        return countCorrectOptionsByExerciseId(exerciseId) > 0;
    }

    /**
     * Get options statistics for exercise
     */
    public OptionStats getOptionStatsForExercise(Long exerciseId) {
        long total = countOptionsByExerciseId(exerciseId);
        long correct = countCorrectOptionsByExerciseId(exerciseId);
        long incorrect = countIncorrectOptionsByExerciseId(exerciseId);

        return new OptionStats(total, correct, incorrect);
    }

    // Inner class for option statistics
    public static class OptionStats {
        private final long total;
        private final long correct;
        private final long incorrect;

        public OptionStats(long total, long correct, long incorrect) {
            this.total = total;
            this.correct = correct;
            this.incorrect = incorrect;
        }

        public long getTotal() {
            return total;
        }

        public long getCorrect() {
            return correct;
        }

        public long getIncorrect() {
            return incorrect;
        }
    }
}
