package com.example.iquiz.dto;

import java.util.UUID;

public record LessonConfigDto(
        UUID id,
        UUID lessonId,
        int questionsPerAttempt,
        int passThreshold,
        boolean noRepeatScope
) {}
