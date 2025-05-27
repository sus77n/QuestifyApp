package com.example.questifyapp.dto;

import com.example.questifyapp.entity.Lesson;

public record LessonDTO(
        Long id,
        String title
) {
    public static LessonDTO fromEntity(Lesson lesson) {
        return new LessonDTO(
                lesson.getId(),
                lesson.getTitle()
        );
    }
}
