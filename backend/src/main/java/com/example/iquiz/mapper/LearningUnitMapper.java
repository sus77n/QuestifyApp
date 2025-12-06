package com.example.iquiz.mapper;

import com.example.iquiz.dto.learningUnit.CourseDto;
import com.example.iquiz.dto.learningUnit.CreateExerciseCategoryDto;
import com.example.iquiz.dto.learningUnit.LearningUnitDto;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.utility.LearningUnitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class LearningUnitMapper {
    @Autowired
    private LearningUnitUtil learningUnitUtil;

    public LearningUnitDto toDto(LearningUnit unit) {
        if (unit == null) {
            return null;
        }

        if (unit.getChildren() == null) {
            return new LearningUnitDto(
                    unit.getId(),
                    unit.getName(),
                    unit.getCode(),
                    unit.getDescription(),
                    unit.getType().getName(),
                    unit.getStatus(),
                    unit.getCreatedAt(),
                    unit.getCreatedBy().getFirstName() + " " + unit.getCreatedBy().getLastName(),
                    unit.getParent() != null ? unit.getParent().getId() : null,
                    null,
                    null,
                    learningUnitUtil.countExercises(unit)
            );

        }

        return new LearningUnitDto(
                unit.getId(),
                unit.getName(),
                unit.getCode(),
                unit.getDescription(),
                unit.getType().getName(),
                unit.getStatus(),
                unit.getCreatedAt(),
                unit.getCreatedBy().getFirstName() + " " + unit.getCreatedBy().getLastName(),
                unit.getParent() != null ? unit.getParent().getId() : null,
                unit.getChildren().stream().map(learningUnit -> toDto(learningUnit)).toList(),
                null,
                learningUnitUtil.countExercises(unit)
        );
    }

    public LearningUnitDto toDtoWithAuth(LearningUnit unit, UUID userId) {
        if (unit == null) {
            return null;
        }

        if (unit.getChildren() == null) {
            return new LearningUnitDto(
                    unit.getId(),
                    unit.getName(),
                    unit.getCode(),
                    unit.getDescription(),
                    unit.getType().getName(),
                    unit.getStatus(),
                    unit.getCreatedAt(),
                    unit.getCreatedBy().getFirstName() + " " + unit.getCreatedBy().getLastName(),
                    unit.getParent() != null ? unit.getParent().getId() : null,
                    null,
                    null,
                    learningUnitUtil.countExercises(unit)
            );

        }

        return new LearningUnitDto(
                unit.getId(),
                unit.getName(),
                unit.getCode(),
                unit.getDescription(),
                unit.getType().getName(),
                unit.getStatus(),
                unit.getCreatedAt(),
                unit.getCreatedBy().getFirstName() + " " + unit.getCreatedBy().getLastName(),
                unit.getParent() != null ? unit.getParent().getId() : null,
                unit.getChildren().stream().map(learningUnit -> toDtoWithAuth(learningUnit, userId)).toList(),
                learningUnitUtil.getNumberOfCompletedExercise(userId, unit),
                learningUnitUtil.countExercises(unit)
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

    public LearningUnitDto toDtoShallow(LearningUnit unit) {
        return new LearningUnitDto(
                unit.getId(),
                unit.getName(),
                unit.getCode(),
                unit.getDescription(),
                unit.getType().getName(),
                unit.getStatus(),
                unit.getCreatedAt(),
                unit.getCreatedBy().getFirstName() + " " + unit.getCreatedBy().getLastName(),
                unit.getParent() != null ? unit.getParent().getId() : null,
                null,
                null,
                learningUnitUtil.countExercises(unit)
        );
    }

    public LearningUnit courseDtoToEntity(CourseDto dto) {
        if (dto == null) {
            return null;
        }

        LearningUnit unit = new LearningUnit();
        unit.setName(dto.name());
        unit.setCode(dto.code());
        unit.setDescription(dto.description());
        unit.setStatus(dto.status());
        return unit;
    }

    public CourseDto toCourseDto(LearningUnit entity) {
        if (entity == null) {
            return null;
        }

        return new CourseDto(
                entity.getId(),
                entity.getName(),
                entity.getCode(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedBy().getFirstName() + " " + entity.getCreatedBy().getLastName(),
                entity.getCreatedAt()
        );
    }

    public LearningUnit generatedCategoryToEntity(CreateExerciseCategoryDto dto) {
        if (dto == null) {
            return null;
        }

        LearningUnit unit = new LearningUnit();
        unit.setName(dto.name());
        unit.setCode(dto.code());
        unit.setDescription(dto.description());
        unit.setStatus(1);

        return unit;
    }
}
