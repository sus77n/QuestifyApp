package com.example.questifyapp.dto.exercise;

import com.example.questifyapp.dto.option.OptionRequestDto;
import com.example.questifyapp.dto.option.OptionResponseDto;
import com.example.questifyapp.dto.learningUnit.LearningUnitChildDto;

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
