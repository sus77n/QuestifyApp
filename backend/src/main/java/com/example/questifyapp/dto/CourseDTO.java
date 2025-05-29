package com.example.questifyapp.dto;

import com.example.questifyapp.entity.Course;

public record CourseDTO(
        Long id,
        String name,
        String description,
        String code,
        int totalChapter
) {
    public static CourseDTO fromEntity(Course course) {
        return new CourseDTO(course.getId(),
                course.getName(),
                course.getDescription(),
                course.getCode(),
                course.getChapters().size());
    }
}
