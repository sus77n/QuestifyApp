package com.example.iquiz.dto.attempt;

import com.example.iquiz.dto.exercise.ExerciseResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AttemptStartResponseDto(
        UUID attemptId,
        UUID userId,
        UUID lessonId,
        int attemptNo,
        LocalDateTime startedAt,
        List<ExerciseResponseDto> questions
) {
}
