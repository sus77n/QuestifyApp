package com.example.questifyapp.dto.option;

import com.example.questifyapp.dto.exercise.ExerciseRequestDto;

public record OptionRequestDto(
        Long id,
        String text,
        boolean isCorrect,
        Long exerciseId
) {
}
