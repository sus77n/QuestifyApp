package com.example.iquiz.dto.learningUnit;

import java.util.UUID;

public record LearningUnitChildDto(
        UUID id,
        String name,
        String code,
        String type,
        long numberOfComplete,
        long numberOfExercise
) {
}
