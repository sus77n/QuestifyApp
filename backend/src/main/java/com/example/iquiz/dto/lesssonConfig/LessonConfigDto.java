package com.example.iquiz.dto.lesssonConfig;

public record LessonConfigDto(
        Long lessonId,
        int questionsPerAttempt,
        boolean noRepeatScope
) {}
