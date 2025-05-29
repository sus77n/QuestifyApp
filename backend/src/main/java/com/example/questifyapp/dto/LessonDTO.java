package com.example.questifyapp.dto;

import com.example.questifyapp.entity.Lesson;

import java.util.List;

public record LessonDTO(
        Long id,
        String title,
        List<ExerciseDTO> exercises
) {
    public static LessonDTO fromEntity(Lesson lesson) {
        return new LessonDTO(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getExercises().stream().map(ExerciseDTO::fromEntity).toList()
        );
    }
}
