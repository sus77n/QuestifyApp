package com.example.questifyapp.dto;

import com.example.questifyapp.entity.LearningUnit;
import java.time.LocalDateTime;
import java.util.List;

public record LearningUnitDto(
        Long id,
        String title,
        String description,
        String type,
        int status,
        int level,
        LocalDateTime createdAt,
        List<LearningUnitDto> childUnits
) {
    public static LearningUnitDto fromEntity(LearningUnit learningUnit) {
        return new LearningUnitDto(
                learningUnit.getId(),
                learningUnit.getTitle(),
                learningUnit.getDescription(),
                learningUnit.getType(),
                learningUnit.getStatus(),
                learningUnit.getLevel(),
                learningUnit.getCreatedAt(),
                learningUnit.getChildUnits().stream()
                        .map(LearningUnitDto::fromEntity)
                        .toList()
        );
    }
}
