package com.example.iquiz.dto.attemptDetail;

import com.example.iquiz.enums.ExerciseType;

import java.math.BigDecimal;
import java.util.UUID;

public record AttemptDetailDto(
        UUID id,
        UUID exerciseId,
        ExerciseType exerciseType,
        String question,
        String userAnswerJson,
        BigDecimal score
) {
}
