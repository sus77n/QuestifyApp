package com.example.iquiz.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public record ExportedCategoryDto(
        UUID categoryId,
        String categoryName,
        List<GeneratedExerciseDto> exercises
) {
    public record GeneratedExerciseDto(
            UUID id,
            String question,
            String type,
            Integer difficulty,

            @JsonProperty("predefinedAnswers")
            List<GeneratedOptionDto> options,

            GeneratedAnswerWrapperDto correctAnswerJson
    ) {}

    public record GeneratedOptionDto(
            UUID id,
            String text,
            String header,
            String metadata
    ) {}

    public record GeneratedAnswerWrapperDto(
            List<Object> correctAnswers
    ) {}
}