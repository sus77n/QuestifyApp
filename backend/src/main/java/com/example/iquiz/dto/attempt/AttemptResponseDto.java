package com.example.iquiz.dto.attempt;

import com.example.iquiz.dto.attemptDetail.ResultDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AttemptResponseDto(
        UUID attemptId,
        UUID userId,
        UUID lessonId,
        BigDecimal score,
        String status,
        LocalDateTime submittedAt,
        List<ResultDto> feedbacks
) {}
