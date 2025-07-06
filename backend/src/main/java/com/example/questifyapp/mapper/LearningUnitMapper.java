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
                learningUnit.getChildUnits().stream().map(LearningUnitMapper::toDto).toList(),
                CourseMapper.tDto(learningUnit.getCourse())
        );
    }

    public static LearningUnit toEntity(LearningUnitDto learningUnitDto) {
        return new LearningUnit(
                learningUnitDto.id(),
                learningUnitDto.title(),
                learningUnitDto.description(),
                learningUnitDto.type(),
                learningUnitDto.status(),
                learningUnitDto.level(),
                learningUnitDto.createdAt(),
                learningUnitDto.childUnits().stream().map(LearningUnitMapper::toEntity).toList(),
                CourseMapper.toEntity(learningUnitDto.course())
        );
    }
}
