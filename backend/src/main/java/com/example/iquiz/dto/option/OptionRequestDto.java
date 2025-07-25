package com.example.iquiz.dto.option;

public record OptionRequestDto(
        Long id,
        String text,
        boolean isCorrect,
        Long exerciseId
) {
}
