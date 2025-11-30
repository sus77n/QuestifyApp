package com.example.iquiz.dto.learningUnit;

import com.example.iquiz.dto.UserDto;
import com.example.iquiz.dto.exercise.ExerciseResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LearningUnitDto(
        UUID id,
        String name,
        String code,
        String description,
        String type,
        int status,
        LocalDateTime createdAt,
        String createdBy,
        UUID parentId,
        List<LearningUnitDto> childUnits,
        Long numberOfComplete,
        long numberOfExercise
) {}
