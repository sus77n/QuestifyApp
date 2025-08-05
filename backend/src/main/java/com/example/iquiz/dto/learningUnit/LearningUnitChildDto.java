package com.example.iquiz.dto.learningUnit;

public record LearningUnitChildDto(
        Long id,
        String name,
        String type,
        long numberOfComplete,
        long numberOfExercise
) {
}
