package com.example.iquiz.dto.exercise;

import com.example.iquiz.dto.option.OptionRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public record ExerciseRequestDto(
        String question,
        String type,
        String answer,
        int difficulty,
        List<OptionRequestDto> options,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long parentUnitId
) {
}
