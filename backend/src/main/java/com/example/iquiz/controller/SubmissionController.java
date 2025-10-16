package com.example.iquiz.controller;

import com.example.iquiz.dto.submission.SubmissionBulkResponseDto;
import com.example.iquiz.dto.submission.SubmissionDto;
import com.example.iquiz.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    // Nộp 1 bài
    @PostMapping
    public ResponseEntity<SubmissionDto> submit(@RequestBody SubmissionDto submissionDto) {
        return ResponseEntity.ok(submissionService.submit(submissionDto));
    }

    // Lấy submission mới nhất của user cho 1 exercise
    @GetMapping("/latest")
    public ResponseEntity<SubmissionDto> getLatestSubmission(
            @RequestParam Long userId,
            @RequestParam Long exerciseId) {
        return ResponseEntity.ok(
                submissionService.getSubmissionByUserIdAndExerciseId(userId, exerciseId)
        );
    }

    // Nộp nhiều bài
    @PostMapping("/bulk")
    public ResponseEntity<SubmissionBulkResponseDto> submitBulk(
            @RequestBody List<SubmissionDto> submissions) {
        return ResponseEntity.ok(submissionService.submitAll(submissions));
    }
}
