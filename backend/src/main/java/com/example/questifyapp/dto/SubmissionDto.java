package com.example.questifyapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SubmissionDto(
        Long id,
        Long exerciseId,
        Long userId,
        String answer,
        Long selectedOptionId,
        LocalDateTime submittedAt,
        BigDecimal score
) {
}
