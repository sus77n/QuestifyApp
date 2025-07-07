package com.example.questifyapp.service;

import com.example.questifyapp.dto.SubmissionDTO;
import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.entity.Option;
import com.example.questifyapp.entity.Submission;
import com.example.questifyapp.entity.User;
import com.example.questifyapp.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private OptionService optionService;
    @Autowired
    private AuthService authService;
    @Autowired
    private ExerciseService exerciseService;

    // Existing methods (keeping them as-is)
    public BigDecimal gradingSubmissionDTO(SubmissionDTO submission) {
        if (submission.optionId() == null) {
            return BigDecimal.valueOf(50);
        }

        Option option = optionService.getOptionById(submission.optionId());
        if (option.isCorrect()) {
            return BigDecimal.valueOf(100);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public Submission saveOrUpdateSubmission(SubmissionDTO submissionDTO, BigDecimal score) {
        Optional<Submission> existingSubmission =
                submissionRepository.findByStudentIdAndExerciseId(submissionDTO.userId(), submissionDTO.exerciseId());

        Submission submission;
        if (existingSubmission.isPresent()) {
            submission = existingSubmission.get();
            submission.setText(submissionDTO.text());
            submission.setScore(gradingSubmissionDTO(submissionDTO));
        } else {
            Exercise exercise = exerciseService.getExerciseById(submissionDTO.exerciseId());
            User user = authService.getUserById(submissionDTO.userId());
            Option selectedOption = submissionDTO.optionId() != null ?
                optionService.getOptionById(submissionDTO.optionId()) : null;

            submission = new Submission(
                    null, // ID will be generated
                    exercise,
                    user,
                    submissionDTO.text(),
                    score,
                    LocalDateTime.now(),
                    selectedOption
            );
        }

        return submissionRepository.save(submission);
    }

    // New CRUD methods

    /**
     * Get all submissions
     */
    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    /**
     * Get submission by ID
     */
    public Submission getSubmissionById(Long id) {
        return submissionRepository.findById(id).orElse(null);
    }

    /**
     * Get submission by ID with Optional return
     */
    public Optional<Submission> findSubmissionById(Long id) {
        return submissionRepository.findById(id);
    }

    /**
     * Create a new submission
     */
    public Submission createSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    /**
     * Update an existing submission
     */
    public Submission updateSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    /**
     * Delete submission by ID
     */
    public void deleteSubmission(Long id) {
        submissionRepository.deleteById(id);
    }

    /**
     * Check if submission exists by ID
     */
    public boolean existsById(Long id) {
        return submissionRepository.existsById(id);
    }

    // Search and filter methods

    /**
     * Get submissions by student ID
     */
    public List<Submission> getSubmissionsByStudentId(Long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }

    /**
     * Get submissions by exercise ID
     */
    public List<Submission> getSubmissionsByExerciseId(Long exerciseId) {
        return submissionRepository.findByExerciseId(exerciseId);
    }

    /**
     * Get submissions by student and exercise
     */
    public Optional<Submission> getSubmissionByStudentAndExercise(Long studentId, Long exerciseId) {
        return submissionRepository.findByStudentIdAndExerciseId(studentId, exerciseId);
    }

    /**
     * Get submissions by score
     */
    public List<Submission> getSubmissionsByScore(BigDecimal score) {
        return submissionRepository.findByScore(score);
    }

    /**
     * Get submissions by score range
     */
    public List<Submission> getSubmissionsByScoreRange(BigDecimal minScore, BigDecimal maxScore) {
        return submissionRepository.findByScoreBetween(minScore, maxScore);
    }

    /**
     * Get submissions with score greater than
     */
    public List<Submission> getSubmissionsWithScoreGreaterThan(BigDecimal score) {
        return submissionRepository.findByScoreGreaterThan(score);
    }

    /**
     * Get submissions with score greater than or equal
     */
    public List<Submission> getSubmissionsWithScoreGreaterThanEqual(BigDecimal score) {
        return submissionRepository.findByScoreGreaterThanEqual(score);
    }

    /**
     * Get submissions with score less than
     */
    public List<Submission> getSubmissionsWithScoreLessThan(BigDecimal score) {
        return submissionRepository.findByScoreLessThan(score);
    }

    /**
     * Get submissions with score less than or equal
     */
    public List<Submission> getSubmissionsWithScoreLessThanEqual(BigDecimal score) {
        return submissionRepository.findByScoreLessThanEqual(score);
    }

    /**
     * Search submissions by text
     */
    public List<Submission> searchSubmissionsByText(String text) {
        return submissionRepository.findByTextContainingIgnoreCase(text);
    }

    /**
     * Search submissions by text (custom query)
     */
    public List<Submission> searchByText(String searchTerm) {
        return submissionRepository.searchByText(searchTerm);
    }

    /**
     * Get submissions submitted after date
     */
    public List<Submission> getSubmissionsSubmittedAfter(LocalDateTime date) {
        return submissionRepository.findBySubmittedAtAfter(date);
    }

    /**
     * Get submissions submitted before date
     */
    public List<Submission> getSubmissionsSubmittedBefore(LocalDateTime date) {
        return submissionRepository.findBySubmittedAtBefore(date);
    }

    /**
     * Get submissions submitted between dates
     */
    public List<Submission> getSubmissionsSubmittedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return submissionRepository.findBySubmittedAtBetween(startDate, endDate);
    }

    /**
     * Get submissions by student and score range
     */
    public List<Submission> getSubmissionsByStudentAndScoreRange(Long studentId, BigDecimal minScore, BigDecimal maxScore) {
        return submissionRepository.findByStudentIdAndScoreBetween(studentId, minScore, maxScore);
    }

    /**
     * Get submissions by exercise and score range
     */
    public List<Submission> getSubmissionsByExerciseAndScoreRange(Long exerciseId, BigDecimal minScore, BigDecimal maxScore) {
        return submissionRepository.findByExerciseIdAndScoreBetween(exerciseId, minScore, maxScore);
    }

    /**
     * Get submissions by exercise type
     */
    public List<Submission> getSubmissionsByExerciseType(String exerciseType) {
        return submissionRepository.findByExerciseType(exerciseType);
    }

    // Ordering methods

    /**
     * Get submissions by student ordered by submitted date (newest first)
     */
    public List<Submission> getSubmissionsByStudentOrderedByDate(Long studentId) {
        return submissionRepository.findByStudentIdOrderBySubmittedAtDesc(studentId);
    }

    /**
     * Get submissions by exercise ordered by score (highest first)
     */
    public List<Submission> getSubmissionsByExerciseOrderedByScore(Long exerciseId) {
        return submissionRepository.findByExerciseIdOrderByScoreDesc(exerciseId);
    }

    /**
     * Get submissions by student ordered by score (highest first)
     */
    public List<Submission> getSubmissionsByStudentOrderedByScore(Long studentId) {
        return submissionRepository.findByStudentIdOrderByScoreDesc(studentId);
    }

    /**
     * Get all submissions ordered by submitted date (newest first)
     */
    public List<Submission> getAllSubmissionsOrderedByDate() {
        return submissionRepository.findAllByOrderBySubmittedAtDesc();
    }

    /**
     * Get all submissions ordered by score (highest first)
     */
    public List<Submission> getAllSubmissionsOrderedByScore() {
        return submissionRepository.findAllByOrderByScoreDesc();
    }

    /**
     * Get top submissions by exercise (highest scores)
     */
    public List<Submission> getTopSubmissionsByExercise(Long exerciseId) {
        return submissionRepository.findTopSubmissionsByExerciseId(exerciseId);
    }

    /**
     * Get latest submission by student and exercise
     */
    public Submission getLatestSubmissionByStudentAndExercise(Long studentId, Long exerciseId) {
        List<Submission> submissions = submissionRepository.findLatestSubmissionByStudentAndExercise(studentId, exerciseId);
        return submissions.isEmpty() ? null : submissions.get(0);
    }

    // Count methods

    /**
     * Count total submissions
     */
    public long countSubmissions() {
        return submissionRepository.count();
    }

    /**
     * Count submissions by student
     */
    public long countSubmissionsByStudentId(Long studentId) {
        return submissionRepository.countByStudentId(studentId);
    }

    /**
     * Count submissions by exercise
     */
    public long countSubmissionsByExerciseId(Long exerciseId) {
        return submissionRepository.countByExerciseId(exerciseId);
    }

    /**
     * Count submissions by score
     */
    public long countSubmissionsByScore(BigDecimal score) {
        return submissionRepository.countByScore(score);
    }

    /**
     * Count submissions by score range
     */
    public long countSubmissionsByScoreRange(BigDecimal minScore, BigDecimal maxScore) {
        return submissionRepository.countByScoreBetween(minScore, maxScore);
    }

    /**
     * Count submissions with score greater than
     */
    public long countSubmissionsWithScoreGreaterThan(BigDecimal score) {
        return submissionRepository.countByScoreGreaterThan(score);
    }

    /**
     * Count submissions with score greater than or equal
     */
    public long countSubmissionsWithScoreGreaterThanEqual(BigDecimal score) {
        return submissionRepository.countByScoreGreaterThanEqual(score);
    }

    // Special query methods

    /**
     * Get passing submissions (score >= 50)
     */
    public List<Submission> getPassingSubmissions() {
        return submissionRepository.findPassingSubmissions();
    }

    /**
     * Get failing submissions (score < 50)
     */
    public List<Submission> getFailingSubmissions() {
        return submissionRepository.findFailingSubmissions();
    }

    /**
     * Get perfect submissions (score = 100)
     */
    public List<Submission> getPerfectSubmissions() {
        return submissionRepository.findPerfectSubmissions();
    }

    // Statistics methods

    /**
     * Get average score by student
     */
    public BigDecimal getAverageScoreByStudentId(Long studentId) {
        return submissionRepository.getAverageScoreByStudentId(studentId);
    }

    /**
     * Get average score by exercise
     */
    public BigDecimal getAverageScoreByExerciseId(Long exerciseId) {
        return submissionRepository.getAverageScoreByExerciseId(exerciseId);
    }

    /**
     * Get highest score by student
     */
    public BigDecimal getHighestScoreByStudentId(Long studentId) {
        return submissionRepository.getHighestScoreByStudentId(studentId);
    }

    /**
     * Get highest score by exercise
     */
    public BigDecimal getHighestScoreByExerciseId(Long exerciseId) {
        return submissionRepository.getHighestScoreByExerciseId(exerciseId);
    }

    /**
     * Count passing submissions by student
     */
    public long countPassingSubmissionsByStudentId(Long studentId) {
        return submissionRepository.countPassingSubmissionsByStudentId(studentId);
    }

    /**
     * Count passing submissions by exercise
     */
    public long countPassingSubmissionsByExerciseId(Long exerciseId) {
        return submissionRepository.countPassingSubmissionsByExerciseId(exerciseId);
    }

    // Business logic methods

    /**
     * Check if exercise has submissions with passing scores
     */
    public boolean hasPassingSubmissions(Long exerciseId) {
        return submissionRepository.existsByExerciseIdAndScoreBetween50And100(exerciseId);
    }

    /**
     * Get submission statistics for student
     */
    public SubmissionStats getSubmissionStatsForStudent(Long studentId) {
        long total = countSubmissionsByStudentId(studentId);
        long passing = countPassingSubmissionsByStudentId(studentId);
        BigDecimal average = getAverageScoreByStudentId(studentId);
        BigDecimal highest = getHighestScoreByStudentId(studentId);

        return new SubmissionStats(total, passing, average, highest);
    }

    /**
     * Get submission statistics for exercise
     */
    public SubmissionStats getSubmissionStatsForExercise(Long exerciseId) {
        long total = countSubmissionsByExerciseId(exerciseId);
        long passing = countPassingSubmissionsByExerciseId(exerciseId);
        BigDecimal average = getAverageScoreByExerciseId(exerciseId);
        BigDecimal highest = getHighestScoreByExerciseId(exerciseId);

        return new SubmissionStats(total, passing, average, highest);
    }

    // Inner class for submission statistics
    public static class SubmissionStats {
        private final long total;
        private final long passing;
        private final BigDecimal average;
        private final BigDecimal highest;

        public SubmissionStats(long total, long passing, BigDecimal average, BigDecimal highest) {
            this.total = total;
            this.passing = passing;
            this.average = average != null ? average : BigDecimal.ZERO;
            this.highest = highest != null ? highest : BigDecimal.ZERO;
        }

        public long getTotal() { return total; }
        public long getPassing() { return passing; }
        public long getFailing() { return total - passing; }
        public BigDecimal getAverage() { return average; }
        public BigDecimal getHighest() { return highest; }
        public double getPassingRate() { return total > 0 ? (double) passing / total * 100 : 0; }
    }
}
