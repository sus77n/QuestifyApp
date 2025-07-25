package com.example.iquiz.dto;

import com.example.iquiz.dto.exercise.ExerciseResponseDto;

import java.util.List;

public record ExerciseTypeDto(
        Long id,
        String code,
        List<ExerciseResponseDto> exercise
) {
}
