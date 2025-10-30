package com.example.iquiz.dto.attempt;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AttemptResponseDto(
        Long attemptId,
        Long userId,
        Long lessonId,
        BigDecimal score,
        String status,
        LocalDateTime submittedAt,
        List<FeedbackDto> feedbacks
) {
    public record FeedbackDto(
            Long exerciseId,
            String question,
            boolean correct,
            String userAnswer,
            String expectedAnswer
    ) {}
}
