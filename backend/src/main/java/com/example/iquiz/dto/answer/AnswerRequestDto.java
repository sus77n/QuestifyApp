package com.example.iquiz.dto.answer;

import java.util.UUID;

public record AnswerRequestDto(
        UUID id,
        String text,
        UUID exerciseId,
        String header,
        String metadata
) {
}
