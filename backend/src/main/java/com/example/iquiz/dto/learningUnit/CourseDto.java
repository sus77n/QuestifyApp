package com.example.iquiz.dto.learningUnit;

import java.time.LocalDateTime;
import java.util.UUID;

public record CourseDto(
        UUID id,
        String name,
        String code,
        String description,
        int status,
        String createdBy,
        LocalDateTime createdAt
) {}
