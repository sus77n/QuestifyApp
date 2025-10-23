package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.dto.submission.SubmissionBulkResponseDto;
import com.example.iquiz.dto.submission.SubmissionDto;
import com.example.iquiz.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    @Autowired
    SubmissionService submissionService;

    // Nộp 1 bài
    @PostMapping
    public ApiResponse<SubmissionDto> submit(@RequestBody SubmissionDto submissionDto) {
        return ApiResponse.success(submissionService.submit(submissionDto), "Submission successful");
    }

    // Lấy submission mới nhất của user cho 1 exercise
    @GetMapping("/latest")
    public ApiResponse<SubmissionDto> getLatestSubmission(
            @RequestParam Long userId,
            @RequestParam Long exerciseId) {
        return ApiResponse.success(
                submissionService.getSubmissionByUserIdAndExerciseId(userId, exerciseId),
                "Latest submission retrieved successfully"
        );
    }

    // Nộp nhiều bài
    @PostMapping("/bulk")
    public ApiResponse<SubmissionBulkResponseDto> submitBulk(
            @RequestBody List<SubmissionDto> submissions) {
        return ApiResponse.success(submissionService.submitAll(submissions), "Bulk submission successful");
    }
}
