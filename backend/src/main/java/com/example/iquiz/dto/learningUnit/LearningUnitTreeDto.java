package com.example.iquiz.dto.learningUnit;

import java.time.LocalDateTime;
import java.util.List;

public record LearningUnitTreeDto(
        Long id,
        String name,
        String code,
        String description,
        String type,        // Course / Chapter / Lesson
        int status,
        String createdBy,
        long numberOfExercises,
        LocalDateTime createdAt,
        List<ChildDto> children // Chapter hoặc Lesson
) {
    public record ChildDto(
            Long id,
            String name,
            String code,
            String description,
            String type,    // Chapter / Lesson
            int status,
            LocalDateTime createdAt,
            Long parentId,
            List<GrandChildDto> children // Nếu là Chapter thì children = lessons
    ) {}

    public record GrandChildDto(
            Long id,
            String name,
            String code,
            String description,
            String type,  // Lesson
            int status,
            LocalDateTime createdAt,
            Long parentId
    ) {}
}
