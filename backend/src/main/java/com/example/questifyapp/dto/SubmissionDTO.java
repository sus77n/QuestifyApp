package com.example.questifyapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SubmissionDTO(
        Long id,
        Long exerciseId,
        Long userId,
        String text,
        Long optionId,
        LocalDateTime submittedAt,
        BigDecimal score,
        ExerciseDTO exercise,
        OptionDTO selectedOption
) {}
