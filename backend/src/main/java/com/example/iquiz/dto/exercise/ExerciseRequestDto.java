package com.example.iquiz.dto.exercise;

import com.example.iquiz.dto.answer.OptionDto;
import com.example.iquiz.enums.ExerciseType;

import java.util.List;
import java.util.UUID;

public record ExerciseRequestDto(
        String question,
        ExerciseType type,
        String correctAnswers,
        Integer difficulty,
        List<OptionDto> options,
        UUID parentId
) {
}