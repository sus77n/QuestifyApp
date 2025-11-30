package com.example.iquiz.dto.learningUnit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LearningUnitTreeDto(
        UUID id,
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
            UUID id,
            String name,
            String code,
            String description,
            String type,    // Chapter / Lesson
            int status,
            LocalDateTime createdAt,
            UUID parentId,
            List<GrandChildDto> children // Nếu là Chapter thì children = lessons
    ) {}

    public record GrandChildDto(
            UUID id,
            String name,
            String code,
            String description,
            String type,  // Lesson
            int status,
            LocalDateTime createdAt,
            UUID parentId
    ) {}
}
