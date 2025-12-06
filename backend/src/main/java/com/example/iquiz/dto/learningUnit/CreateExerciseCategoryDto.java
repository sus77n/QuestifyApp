package com.example.iquiz.dto.learningUnit;

public record CreateExerciseCategoryDto(
        String name,
        String code,
        String type,
        String description,
        long numberOfExercise
) {
}