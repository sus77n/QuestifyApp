package com.example.iquiz.dto.answer;

import java.util.UUID;

public record OptionDto(
        UUID id,
        String text,
        String header,
        String metadata
) {}