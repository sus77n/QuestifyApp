package com.example.iquiz.dto.learningUnit;

public record FullLearningUnitDto(
    String name,
    String code,
    String description,
    String type,
    int status,
    String createdBy,
    long numberOfExercises
) {
}
