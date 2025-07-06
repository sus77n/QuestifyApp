package com.example.questifyapp.mapper;

import com.example.questifyapp.dto.LearningUnitDto;
import com.example.questifyapp.entity.LearningUnit;

public class LearningUnitMapper {

    public static LearningUnitDto toDto(LearningUnit learningUnit) {
        return new LearningUnitDto(
                learningUnit.getId(),
                learningUnit.getTitle(),
                learningUnit.getDescription(),
                learningUnit.getType(),
                learningUnit.getStatus(),
                learningUnit.getLevel(),
                learningUnit.getCreatedAt(),
                learningUnit.getChildUnits().stream().map(LearningUnitMapper::toDto).toList()
        );
    }

    public static LearningUnit toEntity(LearningUnitDto learningUnitDto) {
        LearningUnit learningUnit = new LearningUnit();
        learningUnit.setId(learningUnitDto.id());
        learningUnit.setTitle(learningUnitDto.title());
        learningUnit.setDescription(learningUnitDto.description());
        learningUnit.setType(learningUnitDto.type());
        learningUnit.setStatus(learningUnitDto.status());
        learningUnit.setLevel(learningUnitDto.level());
        learningUnit.setCreatedAt(learningUnitDto.createdAt());
        learningUnit.setChildUnits(
                learningUnitDto.childUnits().stream()
                        .map(LearningUnitMapper::toEntity)
                        .toList()
        );
        return learningUnit;
    }
}
