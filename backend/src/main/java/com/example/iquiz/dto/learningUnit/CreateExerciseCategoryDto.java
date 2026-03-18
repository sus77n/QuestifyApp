package com.example.iquiz.dto.learningUnit;

import java.util.List;
import java.util.UUID;

public record CreateExerciseCategoryDto(
        String name,
        String code,
        String type,
        String description,
        List<UUID> exerciseIds
) {
}