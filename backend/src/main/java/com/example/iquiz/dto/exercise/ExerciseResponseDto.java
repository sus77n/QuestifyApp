package com.example.iquiz.dto.exercise;

import com.example.iquiz.dto.option.OptionResponseDto;

import java.util.List;

public record ExerciseResponseDto(
        Long id,
        String question,
        String type,
        List<OptionResponseDto> options
) {}

