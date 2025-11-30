package com.example.iquiz.dto.learningUnit;

import java.util.UUID;

public record CreateLearningUnitChildDto(
        String name,
        UUID parentId
) {}
