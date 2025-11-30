package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.dto.attempt.AttemptResponseDto;
import com.example.iquiz.dto.attempt.AttemptStartResponseDto;
import com.example.iquiz.dto.attemptDetail.AttemptDetailDto;
import com.example.iquiz.entity.Attempt;
import com.example.iquiz.service.AttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attempts")
@RequiredArgsConstructor
public class AttemptController {

    @Autowired
    AttemptService attemptService;

    @PostMapping
    public ApiResponse<Attempt> create(@RequestBody Attempt attempt) {
        return ApiResponse.success(attemptService.save(attempt), "Attempt created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<Attempt> getById(@PathVariable UUID id) {
        return ApiResponse.success(attemptService.findById(id), "Attempt found successfully");
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Attempt>> getByUser(@PathVariable UUID userId) {
        return ApiResponse.success(attemptService.findByUser(userId), "Attempts for user found successfully");
    }

    @GetMapping("/lesson/{lessonId}")
    public ApiResponse<List<Attempt>> getByLesson(@PathVariable UUID lessonId) {
        return ApiResponse.success(attemptService.findByLesson(lessonId), "Attempts for lesson found successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        attemptService.delete(id);
        return ApiResponse.success(null, "Attempt deleted successfully");
    }

    @PostMapping("/start")
    public ApiResponse<AttemptStartResponseDto> startAttempt(
            @RequestParam UUID userId,
            @RequestParam UUID lessonId
    ) {
        return ApiResponse.success(attemptService.startAttempt(userId, lessonId), "Attempt started successfully");
    }


    @PostMapping("/{attemptId}/submit")
    public ApiResponse<AttemptResponseDto> submitAttempt(
            @PathVariable UUID attemptId,
            @RequestBody List<AttemptDetailDto> submissions
    ) {
        return ApiResponse.success(attemptService.submitAttempt(attemptId, submissions), "Attempt submitted successfully");
    }
}
