package com.example.questifyapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public record LearningUnitDto(
        Long id,
        String title,
        String description,
        String type,
        int status,
        int level,
        LocalDateTime createdAt,
        List<LearningUnitDto> childUnits,
        CourseDTO course
) {
}
