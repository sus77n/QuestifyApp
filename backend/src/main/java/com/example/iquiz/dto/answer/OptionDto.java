package com.example.iquiz.dto.answer;

import java.util.Map;
import java.util.UUID;

public record OptionDto(
        UUID id,
        String text,
        String header,
        Map<String, String> metadata
) {}