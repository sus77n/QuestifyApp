package com.example.iquiz.dto.exercise;

import com.example.iquiz.dto.answer.OptionDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ExerciseWithAnswerDto(
        UUID id,
        String question,
        String type,
        Integer difficulty,
        List<OptionDto> options,
        String correctAnswers,
        UUID parentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
