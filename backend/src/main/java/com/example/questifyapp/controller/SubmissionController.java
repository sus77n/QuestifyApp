package com.example.questifyapp.controller;

import com.example.questifyapp.dto.SubmissionDto;
import com.example.questifyapp.service.OptionService;
import com.example.questifyapp.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {
    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private OptionService optionService;

    @PostMapping("/submit")
    public ResponseEntity<SubmissionDto> submitAnExercise(@RequestBody SubmissionDto submissionDTO) {
        return ResponseEntity.ok(submissionService.submit(submissionDTO));
    }


}
