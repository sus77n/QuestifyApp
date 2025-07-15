package com.example.questifyapp.dto.learningUnit;

public record LearningUnitChildDto(
        Long id,
        String name,
        String type,
        Long numberOfExercise
) {
}
