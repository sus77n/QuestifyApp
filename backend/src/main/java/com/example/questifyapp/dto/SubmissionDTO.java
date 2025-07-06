package com.example.questifyapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SubmissionDTO(
        Long id,
        ExerciseDTO exercise,
        Long userId,
        String text,
        OptionDTO selectedOption,
        LocalDateTime submittedAt,
        BigDecimal score
) {
}
