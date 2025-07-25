package com.example.iquiz.dto.learningUnit;

public record CourseDto(
        Long id,
        String name,
        String code,
        Long totalOfExercise,
        Long completedExercises
) {}
