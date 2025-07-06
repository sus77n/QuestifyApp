package com.example.questifyapp.dto;

import com.example.questifyapp.entity.Exercise;

import java.time.LocalDateTime;
import java.util.List;

public record ExerciseDTO(
        Long id,
        String question,
        String type,
        List<OptionDTO> options,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LearningUnitDto parentUnit
) {}

