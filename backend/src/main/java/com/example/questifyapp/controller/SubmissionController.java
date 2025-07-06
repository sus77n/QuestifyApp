package com.example.questifyapp.controller;

import com.example.questifyapp.dto.SubmissionDTO;
import com.example.questifyapp.service.OptionService;
import com.example.questifyapp.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {
    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private OptionService optionService;

    @PostMapping("/submit")
    public ResponseEntity<BigDecimal> submitAnExercise(@RequestBody SubmissionDTO submissionDTO) {
        BigDecimal score = new BigDecimal(100);
        return ResponseEntity.ok(score);
    }

}
