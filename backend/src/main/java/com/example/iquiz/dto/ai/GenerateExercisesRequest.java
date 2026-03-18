package com.example.iquiz.dto.ai;

import com.example.iquiz.dto.learningUnit.CreateExerciseCategoryDto;

import java.util.List;
import java.util.UUID;

public record GenerateExercisesRequest(
        UUID lessonId,
        List<CreateExerciseCategoryDto> categories
) {
}
