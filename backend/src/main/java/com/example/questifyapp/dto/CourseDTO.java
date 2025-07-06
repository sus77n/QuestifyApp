package com.example.questifyapp.dto;


import java.util.List;

public record CourseDTO(
        Long id,
        String name,
        String description,
        String code,
        List<LearningUnitDto> learningUnits
) {}
