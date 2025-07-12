package com.example.questifyapp.dto.exercise;

import com.example.questifyapp.dto.option.OptionResponseDto;

import java.util.List;

public record ExerciseResponseDto(
        Long id,
        String question,
        String type,
        List<OptionResponseDto> options
) {}

