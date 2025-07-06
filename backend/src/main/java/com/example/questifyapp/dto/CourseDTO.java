package com.example.questifyapp.dto;

import com.example.questifyapp.entity.Course;

public record CourseDTO(
        Long id,
        String name,
        String description,
        String code,
        int totalChapter
) {
  
}
