package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.dto.attemptDetail.AttemptDetailBulkResponseDto;
import com.example.iquiz.dto.attemptDetail.AttemptDetailDto;
import com.example.iquiz.service.AttemptDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class AttemptDetailController {

    @Autowired
    AttemptDetailService attemptDetailService;

    // Nộp 1 bài
    @PostMapping
    public ApiResponse<AttemptDetailDto> submit(@RequestBody AttemptDetailDto attemptDetailDto) {
        return ApiResponse.success(attemptDetailService.submit(attemptDetailDto), "Submission successful");
    }

    // Nộp nhiều bài
    @PostMapping("/bulk")
    public ApiResponse<AttemptDetailBulkResponseDto> submitBulk(
            @RequestBody List<AttemptDetailDto> submissions) {
        return ApiResponse.success(attemptDetailService.submitAll(submissions), "Bulk submission successful");
    }
}
