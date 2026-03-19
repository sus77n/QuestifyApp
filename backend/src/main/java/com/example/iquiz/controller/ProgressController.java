package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.dto.ProgressDTO;
import com.example.iquiz.security.UserDetailsImpl;
import com.example.iquiz.service.ParticipantProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {
    @Autowired
    private final ParticipantProgressService participantProgressService;

    @GetMapping("/completed")
    public ApiResponse<List<ProgressDTO>> getCompletedCourses(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.success(
                participantProgressService.findCompletedCoursesByParticipantId(userDetails.getId()),
                "Fetched completed courses");
    }

    @GetMapping("/incompleted")
    public ApiResponse<List<ProgressDTO>> getIncompletedCourses(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.success(
                participantProgressService.findIncompletedCoursesByParticipantId(userDetails.getId()),
                "Fetched incomplete courses"
        );
    }

}
