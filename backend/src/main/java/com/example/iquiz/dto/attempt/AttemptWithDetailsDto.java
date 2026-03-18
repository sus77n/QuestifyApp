package com.example.iquiz.dto.attempt;

import com.example.iquiz.dto.attemptDetail.AttemptDetailDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AttemptWithDetailsDto(
        UUID attemptId,
        UUID userId,
        String username,
        String userEmail,
        UUID lessonId,
        String lessonName,
        BigDecimal score,
        String status,
        LocalDateTime startedAt,
        LocalDateTime submittedAt,
        List<AttemptDetailDto> attemptDetails
) {
}
