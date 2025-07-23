package com.example.questifyapp.mapper;

import com.example.questifyapp.dto.LearningUnitTypeDto;
import com.example.questifyapp.entity.LearningUnitType;
import org.springframework.stereotype.Component;

@Component
public class LearningUnitTypeMapper {
    public LearningUnitTypeDto toDto(LearningUnitType entity) {
        if (entity == null) {
            return null;
        }

        return new LearningUnitTypeDto(
                entity.getId(),
                entity.getName(),
                entity.getLevel()
        );
    }

    public LearningUnitType toEntity(LearningUnitTypeDto dto) {
        if (dto == null) {
            return null;
        }

        LearningUnitType entity = new LearningUnitType();
        entity.setName(dto.type());
        entity.setLevel(dto.level());
        entity.setId(dto.id());

        return entity;
    }
}
