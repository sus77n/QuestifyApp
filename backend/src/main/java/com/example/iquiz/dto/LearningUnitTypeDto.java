package com.example.iquiz.dto;

import java.util.UUID;

public record LearningUnitTypeDto (
    UUID id,
    String type,
    int level
) {
}
