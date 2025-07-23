package com.example.questifyapp.controller;

import com.example.questifyapp.dto.submission.SubmissionDto;
import com.example.questifyapp.mapper.SubmissionMapper;
import com.example.questifyapp.service.OptionService;
import com.example.questifyapp.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {
    @Autowired
    private SubmissionService submissionService;

    @PostMapping("/submit")
    public ResponseEntity<SubmissionDto> submitAnExercise(@RequestBody SubmissionDto submissionDTO) {
        return ResponseEntity.ok(submissionService.submit(submissionDTO));
    }

    @GetMapping("/latest")
    public ResponseEntity<SubmissionDto> getLatestSubmission(
            @RequestParam Long userId,
            @RequestParam Long exerciseId) {
        return ResponseEntity.ok(submissionService.getSubmissionByUserIdAndExerciseId(userId, exerciseId));
    }

    @PostMapping("/submit-all")
    public ResponseEntity<Double> submitAllSubmissions(@RequestBody List<SubmissionDto> submissionDTO) {
        return ResponseEntity.ok(submissionService.submitAll(submissionDTO));
    }
}
