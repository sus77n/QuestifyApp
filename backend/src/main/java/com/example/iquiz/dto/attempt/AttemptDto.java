package com.example.iquiz.dto.attempt;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AttemptDto(
        UUID attemptId,
        UUID userId,
        String username,
        String userEmail,
        UUID lessonId,
        String lessonName,
        BigDecimal score,
        String status,
        LocalDateTime startedAt,
        LocalDateTime submittedAt
) {
}
