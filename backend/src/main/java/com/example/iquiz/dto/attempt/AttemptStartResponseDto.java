package com.example.iquiz.dto.attempt;

import java.time.LocalDateTime;
import java.util.List;

public record AttemptStartResponseDto(
        Long attemptId,
        Long userId,
        Long lessonId,
        int attemptNo,
        LocalDateTime startedAt,
        List<QuestionDto> questions
) {
    public record QuestionDto(
            Long id,
            String question,
            List<OptionDto> options
    ) {}

    public record OptionDto(
            Long id,
            String text
    ) {}
}
