package com.example.questifyapp.controller;

import com.example.questifyapp.dto.SubmissionDTO;
import com.example.questifyapp.entity.Submission;
import com.example.questifyapp.mapper.SubmissionMapper;
import com.example.questifyapp.service.OptionService;
import com.example.questifyapp.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {
    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private OptionService optionService;

    // Existing endpoint (keeping it as-is)
    @PostMapping("/submit")
    public ResponseEntity<BigDecimal> submitAnExercise(@RequestBody SubmissionDTO submissionDTO) {
        BigDecimal score = submissionService.gradingSubmissionDTO(submissionDTO);
        submissionService.saveOrUpdateSubmission(submissionDTO, score);
        return ResponseEntity.ok(score);
    }

    // New CRUD endpoints

    /**
     * Get all submissions
     */
    @GetMapping("")
    public ResponseEntity<List<SubmissionDTO>> getAllSubmissions() {
        List<Submission> submissions = submissionService.getAllSubmissions();
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    /**
     * Get submission by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubmissionDTO> getSubmissionById(@PathVariable Long id) {
        Submission submission = submissionService.getSubmissionById(id);
        if (submission == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(SubmissionMapper.toDto(submission));
    }

    /**
     * Create a new submission
     */
    @PostMapping("")
    public ResponseEntity<SubmissionDTO> createSubmission(@RequestBody SubmissionDTO submissionDTO) {
        try {
            Submission submission = SubmissionMapper.toEntity(submissionDTO);
            Submission createdSubmission = submissionService.createSubmission(submission);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(SubmissionMapper.toDto(createdSubmission));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Update an existing submission
     */
    @PutMapping("/{id}")
    public ResponseEntity<SubmissionDTO> updateSubmission(
            @PathVariable Long id,
            @RequestBody SubmissionDTO submissionDTO) {

        if (!submissionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            Submission submission = SubmissionMapper.toEntity(submissionDTO);
            submission.setId(id);
            Submission updatedSubmission = submissionService.updateSubmission(submission);
            return ResponseEntity.ok(SubmissionMapper.toDto(updatedSubmission));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Delete submission by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSubmission(@PathVariable Long id) {
        if (!submissionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            submissionService.deleteSubmission(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Submission deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete submission");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Search and filter endpoints

    /**
     * Get submissions by student ID
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByStudentId(@PathVariable Long studentId) {
        List<Submission> submissions = submissionService.getSubmissionsByStudentId(studentId);
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    /**
     * Get submissions by exercise ID
     */
    @GetMapping("/exercise/{exerciseId}")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByExerciseId(@PathVariable Long exerciseId) {
        List<Submission> submissions = submissionService.getSubmissionsByExerciseId(exerciseId);
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    /**
     * Get submission by student and exercise
     */
    @GetMapping("/student/{studentId}/exercise/{exerciseId}")
    public ResponseEntity<SubmissionDTO> getSubmissionByStudentAndExercise(
            @PathVariable Long studentId,
            @PathVariable Long exerciseId) {

        return submissionService.getSubmissionByStudentAndExercise(studentId, exerciseId)
                .map(submission -> ResponseEntity.ok(SubmissionMapper.toDto(submission)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get submissions by score
     */
    @GetMapping("/score/{score}")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByScore(@PathVariable BigDecimal score) {
        List<Submission> submissions = submissionService.getSubmissionsByScore(score);
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    /**
     * Get submissions by score range
     */
    @GetMapping("/score-range")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByScoreRange(
            @RequestParam BigDecimal minScore,
            @RequestParam BigDecimal maxScore) {

        List<Submission> submissions = submissionService.getSubmissionsByScoreRange(minScore, maxScore);
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    /**
     * Get submissions with score greater than
     */
    @GetMapping("/score-greater-than/{score}")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsWithScoreGreaterThan(@PathVariable BigDecimal score) {
        List<Submission> submissions = submissionService.getSubmissionsWithScoreGreaterThan(score);
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    /**
     * Search submissions by text
     */
    @GetMapping("/search")
    public ResponseEntity<List<SubmissionDTO>> searchSubmissions(@RequestParam String text) {
        List<Submission> submissions = submissionService.searchSubmissionsByText(text);
        if (submissions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    /**
     * Get submissions submitted after date
     */
    @GetMapping("/submitted-after")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsSubmittedAfter(@RequestParam String date) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date);
            List<Submission> submissions = submissionService.getSubmissionsSubmittedAfter(dateTime);
            return ResponseEntity.ok(submissions.stream()
                    .map(SubmissionMapper::toDto)
                    .toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get submissions submitted between dates
     */
    @GetMapping("/submitted-between")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsSubmittedBetween(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startDate);
            LocalDateTime endDateTime = LocalDateTime.parse(endDate);
            List<Submission> submissions = submissionService.getSubmissionsSubmittedBetween(startDateTime, endDateTime);
            return ResponseEntity.ok(submissions.stream()
                    .map(SubmissionMapper::toDto)
                    .toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get submissions by exercise type
     */
    @GetMapping("/exercise-type/{exerciseType}")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByExerciseType(@PathVariable String exerciseType) {
        List<Submission> submissions = submissionService.getSubmissionsByExerciseType(exerciseType);
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    // Ordering endpoints

    /**
     * Get submissions by student ordered by date (newest first)
     */
    @GetMapping("/student/{studentId}/ordered-by-date")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByStudentOrderedByDate(@PathVariable Long studentId) {
        List<Submission> submissions = submissionService.getSubmissionsByStudentOrderedByDate(studentId);
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    /**
     * Get submissions by exercise ordered by score (highest first)
     */
    @GetMapping("/exercise/{exerciseId}/ordered-by-score")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByExerciseOrderedByScore(@PathVariable Long exerciseId) {
        List<Submission> submissions = submissionService.getSubmissionsByExerciseOrderedByScore(exerciseId);
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    /**
     * Get submissions by student ordered by score (highest first)
     */
    @GetMapping("/student/{studentId}/ordered-by-score")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionsByStudentOrderedByScore(@PathVariable Long studentId) {
        List<Submission> submissions = submissionService.getSubmissionsByStudentOrderedByScore(studentId);
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    /**
     * Get all submissions ordered by date (newest first)
     */
    @GetMapping("/ordered-by-date")
    public ResponseEntity<List<SubmissionDTO>> getAllSubmissionsOrderedByDate() {
        List<Submission> submissions = submissionService.getAllSubmissionsOrderedByDate();
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    /**
     * Get all submissions ordered by score (highest first)
     */
    @GetMapping("/ordered-by-score")
    public ResponseEntity<List<SubmissionDTO>> getAllSubmissionsOrderedByScore() {
        List<Submission> submissions = submissionService.getAllSubmissionsOrderedByScore();
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    /**
     * Get top submissions by exercise (highest scores)
     */
    @GetMapping("/exercise/{exerciseId}/top")
    public ResponseEntity<List<SubmissionDTO>> getTopSubmissionsByExercise(@PathVariable Long exerciseId) {
        List<Submission> submissions = submissionService.getTopSubmissionsByExercise(exerciseId);
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    /**
     * Get latest submission by student and exercise
     */
    @GetMapping("/student/{studentId}/exercise/{exerciseId}/latest")
    public ResponseEntity<SubmissionDTO> getLatestSubmissionByStudentAndExercise(
            @PathVariable Long studentId,
            @PathVariable Long exerciseId) {

        Submission submission = submissionService.getLatestSubmissionByStudentAndExercise(studentId, exerciseId);
        if (submission == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(SubmissionMapper.toDto(submission));
    }

    // Special query endpoints

    /**
     * Get passing submissions (score >= 50)
     */
    @GetMapping("/passing")
    public ResponseEntity<List<SubmissionDTO>> getPassingSubmissions() {
        List<Submission> submissions = submissionService.getPassingSubmissions();
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    /**
     * Get failing submissions (score < 50)
     */
    @GetMapping("/failing")
    public ResponseEntity<List<SubmissionDTO>> getFailingSubmissions() {
        List<Submission> submissions = submissionService.getFailingSubmissions();
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    /**
     * Get perfect submissions (score = 100)
     */
    @GetMapping("/perfect")
    public ResponseEntity<List<SubmissionDTO>> getPerfectSubmissions() {
        List<Submission> submissions = submissionService.getPerfectSubmissions();
        return ResponseEntity.ok(submissions.stream()
                .map(SubmissionMapper::toDto)
                .toList());
    }

    // Statistics endpoints

    /**
     * Get total submissions count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalSubmissionsCount() {
        long count = submissionService.countSubmissions();
        Map<String, Long> response = new HashMap<>();
        response.put("total", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get submissions count by student
     */
    @GetMapping("/count/student/{studentId}")
    public ResponseEntity<Map<String, Long>> getSubmissionsCountByStudent(@PathVariable Long studentId) {
        long count = submissionService.countSubmissionsByStudentId(studentId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        response.put("studentId", studentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get submissions count by exercise
     */
    @GetMapping("/count/exercise/{exerciseId}")
    public ResponseEntity<Map<String, Long>> getSubmissionsCountByExercise(@PathVariable Long exerciseId) {
        long count = submissionService.countSubmissionsByExerciseId(exerciseId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        response.put("exerciseId", exerciseId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get average score by student
     */
    @GetMapping("/average-score/student/{studentId}")
    public ResponseEntity<Map<String, BigDecimal>> getAverageScoreByStudent(@PathVariable Long studentId) {
        BigDecimal average = submissionService.getAverageScoreByStudentId(studentId);
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("averageScore", average);
        return ResponseEntity.ok(response);
    }

    /**
     * Get average score by exercise
     */
    @GetMapping("/average-score/exercise/{exerciseId}")
    public ResponseEntity<Map<String, BigDecimal>> getAverageScoreByExercise(@PathVariable Long exerciseId) {
        BigDecimal average = submissionService.getAverageScoreByExerciseId(exerciseId);
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("averageScore", average);
        return ResponseEntity.ok(response);
    }

    /**
     * Get highest score by student
     */
    @GetMapping("/highest-score/student/{studentId}")
    public ResponseEntity<Map<String, BigDecimal>> getHighestScoreByStudent(@PathVariable Long studentId) {
        BigDecimal highest = submissionService.getHighestScoreByStudentId(studentId);
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("highestScore", highest);
        return ResponseEntity.ok(response);
    }

    /**
     * Get highest score by exercise
     */
    @GetMapping("/highest-score/exercise/{exerciseId}")
    public ResponseEntity<Map<String, BigDecimal>> getHighestScoreByExercise(@PathVariable Long exerciseId) {
        BigDecimal highest = submissionService.getHighestScoreByExerciseId(exerciseId);
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("highestScore", highest);
        return ResponseEntity.ok(response);
    }

    /**
     * Get submission statistics for student
     */
    @GetMapping("/stats/student/{studentId}")
    public ResponseEntity<Map<String, Object>> getSubmissionStatsForStudent(@PathVariable Long studentId) {
        SubmissionService.SubmissionStats stats = submissionService.getSubmissionStatsForStudent(studentId);
        Map<String, Object> response = new HashMap<>();
        response.put("studentId", studentId);
        response.put("totalSubmissions", stats.getTotal());
        response.put("passingSubmissions", stats.getPassing());
        response.put("failingSubmissions", stats.getFailing());
        response.put("averageScore", stats.getAverage());
        response.put("highestScore", stats.getHighest());
        response.put("passingRate", stats.getPassingRate());
        return ResponseEntity.ok(response);
    }

    /**
     * Get submission statistics for exercise
     */
    @GetMapping("/stats/exercise/{exerciseId}")
    public ResponseEntity<Map<String, Object>> getSubmissionStatsForExercise(@PathVariable Long exerciseId) {
        SubmissionService.SubmissionStats stats = submissionService.getSubmissionStatsForExercise(exerciseId);
        Map<String, Object> response = new HashMap<>();
        response.put("exerciseId", exerciseId);
        response.put("totalSubmissions", stats.getTotal());
        response.put("passingSubmissions", stats.getPassing());
        response.put("failingSubmissions", stats.getFailing());
        response.put("averageScore", stats.getAverage());
        response.put("highestScore", stats.getHighest());
        response.put("passingRate", stats.getPassingRate());
        return ResponseEntity.ok(response);
    }

    /**
     * Check if exercise has passing submissions
     */
    @GetMapping("/exercise/{exerciseId}/has-passing")
    public ResponseEntity<Map<String, Boolean>> hasPassingSubmissions(@PathVariable Long exerciseId) {
        boolean hasPassing = submissionService.hasPassingSubmissions(exerciseId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasPassingSubmissions", hasPassing);
        return ResponseEntity.ok(response);
    }
}
