package com.example.iquiz.controller;

import com.example.iquiz.dto.attempt.AttemptResponseDto;
import com.example.iquiz.dto.attempt.AttemptStartResponseDto;
import com.example.iquiz.dto.submission.SubmissionDto;
import com.example.iquiz.entity.Attempt;
import com.example.iquiz.service.AttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attempts")
@RequiredArgsConstructor
public class AttemptController {

    private final AttemptService attemptService;

    @PostMapping
    public ResponseEntity<Attempt> create(@RequestBody Attempt attempt) {
        return ResponseEntity.ok(attemptService.save(attempt));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attempt> getById(@PathVariable Long id) {
        return attemptService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Attempt>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(attemptService.findByUser(userId));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<Attempt>> getByLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(attemptService.findByLesson(lessonId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attemptService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/start")
    public ResponseEntity<AttemptStartResponseDto> startAttempt(
            @RequestParam Long userId,
            @RequestParam Long lessonId
    ) {
        return ResponseEntity.ok(attemptService.startAttempt(userId, lessonId));
    }


    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<AttemptResponseDto> submitAttempt(
            @PathVariable Long attemptId,
            @RequestBody List<SubmissionDto> submissions
    ) {
        return ResponseEntity.ok(attemptService.submitAttempt(attemptId, submissions));
    }
}
