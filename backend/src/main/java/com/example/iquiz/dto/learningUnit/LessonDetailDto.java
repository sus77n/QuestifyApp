package com.example.iquiz.dto.learningUnit;

import com.example.iquiz.dto.LessonConfigDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LessonDetailDto(
        UUID id,
        String name,
        String code,
        String description,
        String type,
        int status,
        LocalDateTime createdAt,
        String createdBy,
        UUID parentId,
        List<LearningUnitDto> exerciseCategories,
        LessonConfigDto lessonConfig
) implements LearningUnitDtoInterface {}
