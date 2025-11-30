package com.example.iquiz.dto.lesssonConfig;

import java.util.UUID;

public record LessonConfigDto(
        UUID lessonId,
        int questionsPerAttempt,
        boolean noRepeatScope
) {}
