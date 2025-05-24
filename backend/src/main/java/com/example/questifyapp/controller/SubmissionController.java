package com.example.questifyapp.controller;

import com.example.questifyapp.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/submission")
public class SubmissionController {
    @Autowired
    private SubmissionService submissionService;

    @GetMapping("/totalSubmissionInACourse/{courseId}")
    public long countSubmissionsWithScoreBetween50And100(@PathVariable int courseId) {
        return submissionService.countDistinctSubmissionsByCourseId(courseId);
    }
}
