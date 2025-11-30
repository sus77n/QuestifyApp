package com.example.iquiz.dto.exercise;

import com.example.iquiz.dto.answer.OptionDto;

import java.util.List;
import java.util.UUID;

public record ExerciseResponseDto(
        UUID id,
        String question,
        String type,
        List<OptionDto> options
) {
}