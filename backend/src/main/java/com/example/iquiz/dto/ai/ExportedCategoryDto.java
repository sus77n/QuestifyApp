package com.example.iquiz.dto.ai;

import com.example.iquiz.exception.NullableUUIDDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ExportedCategoryDto(

        @JsonDeserialize(using = NullableUUIDDeserializer.class)
        UUID categoryId,

        String categoryName,

        List<GeneratedExerciseDto> exercises

) {

    public record GeneratedExerciseDto(

            @JsonDeserialize(using = NullableUUIDDeserializer.class)
            UUID id,

            String question,

            String type,

            Integer difficulty,

            @JsonProperty("predefinedAnswers")
            List<GeneratedOptionDto> options,

            GeneratedAnswerWrapperDto correctAnswerJson

    ) {}

    public record GeneratedOptionDto(

            @JsonDeserialize(using = NullableUUIDDeserializer.class)
            UUID id,

            String text,

            String header,

            Map<String, Object> metadata

    ) {}

    public record GeneratedAnswerWrapperDto(

            List<Object> correctAnswers

    ) {}
}