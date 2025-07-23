package com.example.questifyapp.dto;

import com.example.questifyapp.dto.exercise.ExerciseResponseDto;

import java.util.List;

public record ExerciseTypeDto(
        Long id,
        String code,
        List<ExerciseResponseDto> exercise
) {
}
