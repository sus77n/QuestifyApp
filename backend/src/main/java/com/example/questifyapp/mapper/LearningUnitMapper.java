package com.example.questifyapp.mapper;

import com.example.questifyapp.dto.learningUnit.LearningUnitChildDto;
import com.example.questifyapp.dto.learningUnit.LearningUnitDto;
import com.example.questifyapp.entity.LearningUnit;
import com.example.questifyapp.entity.LearningUnitType;

public class LearningUnitMapper {

    public static LearningUnitDto toDto(LearningUnit unit) {
        if (unit == null) {
            return null;
        }
        return new LearningUnitDto(
                unit.getId(),
                unit.getName(),
                unit.getCode(),
                unit.getDescription(),
                LearningUnitTypeMapper.toDto(unit.getType()),
                unit.getStatus(),
                unit.getCreatedAt(),
                UserMapper.toDto(unit.getCreatedBy()),
                unit.getParent() != null ? unit.getParent().getId() : null,
                unit.getChildren().stream().map(LearningUnitMapper::toChildDto).toList()
        );
    }

    public static LearningUnit toEntity(LearningUnitDto dto) {
        if (dto == null) {
            return null;
        }

        LearningUnit unit = new LearningUnit();
        unit.setId(dto.id());
        unit.setName(dto.name());
        unit.setCode(dto.code());
        unit.setDescription(dto.description());
        unit.setStatus(dto.status());
        unit.setCreatedAt(dto.createdAt());
        return unit;
    }

    public static LearningUnitChildDto toChildDto(LearningUnit entity) {
        if (entity == null) {
            return null;
        }

        return new LearningUnitChildDto(
                entity.getId(),
                entity.getName(),
                entity.getType() != null ? entity.getType().getName() : null
        );
    }
}
