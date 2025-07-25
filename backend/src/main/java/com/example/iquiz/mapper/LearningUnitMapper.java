package com.example.iquiz.mapper;

import com.example.iquiz.dto.learningUnit.LearningUnitChildDto;
import com.example.iquiz.dto.learningUnit.LearningUnitDto;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.utility.LearningUnitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LearningUnitMapper {
    @Autowired
    private ExerciseMapper exerciseMapper;
    @Autowired
    private UserMapper userMapper;

    public LearningUnitDto toDto(LearningUnit unit) {
        if (unit == null) {
            return null;
        }
        return new LearningUnitDto(
                unit.getId(),
                unit.getName(),
                unit.getCode(),
                unit.getDescription(),
                unit.getType().getName(),
                unit.getStatus(),
                unit.getCreatedAt(),
                userMapper.toDto(unit.getCreatedBy()),
                unit.getParent() != null ? unit.getParent().getId() : null,
                unit.getChildren().stream().map(learningUnit -> toChildDto(learningUnit)).toList(),
                unit.getExercises().stream().map(exercise -> exerciseMapper.toDto(exercise)).toList(),
                LearningUnitUtil.countExercises(unit)
        );
    }

    public LearningUnit toEntity(LearningUnitDto dto) {
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

    public LearningUnitChildDto toChildDto(LearningUnit entity) {
        if (entity == null) {
            return null;
        }

        return new LearningUnitChildDto(
                entity.getId(),
                entity.getName(),
                entity.getType() != null ? entity.getType().getName() : null,
                LearningUnitUtil.countExercises(entity)
        );
    }
}
