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

//    @GetMapping("/submitted/count")
//    public ResponseEntity<Integer> countSubmitted(@RequestParam Long courseId, @RequestParam Long userId) {
//        List<SubmissionDTO> submissions = submissionService.getSubmissionsByCourseIdAndUserId(courseId, userId);
//        return ResponseEntity.ok(submissions.size());
//    }

}
