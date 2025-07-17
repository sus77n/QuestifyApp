package com.example.questifyapp.dto.learningUnit;

public record CourseDto(
        Long id,
        String name,
        String code,
        Long totalOfExercise,
        Long completedExercises
) {}
