package com.example.iquiz.mapper;

import com.example.iquiz.dto.learningUnit.CourseDto;
import com.example.iquiz.dto.learningUnit.LearningUnitChildDto;
import com.example.iquiz.dto.learningUnit.LearningUnitDto;
import com.example.iquiz.dto.learningUnit.LearningUnitTreeDto;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.utility.LearningUnitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;


@Component
public class LearningUnitMapper {
    @Autowired
    private ExerciseMapper exerciseMapper;
    @Autowired
    private LearningUnitUtil learningUnitUtil;

    public LearningUnitDto toDto(LearningUnit unit) {
        if (unit == null) {
            return null;
        }

        if (unit.getChildren() == null || unit.getChildren().isEmpty()) {
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

    public LearningUnitChildDto toChildDto(LearningUnit entity) {
        if (entity == null) {
            return null;
        }

        return new LearningUnitChildDto(
                entity.getId(),
                entity.getName(),
                entity.getCode(),
                entity.getType() != null ? entity.getType().getName() : null,
                0,
                learningUnitUtil.countExercises(entity)
        );
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

    public LearningUnitTreeDto toTreeDto(LearningUnit course) {
        if (course == null) {
            return null;
        }
        return new LearningUnitTreeDto(
                course.getId(),
                course.getName(),
                course.getCode(),
                course.getDescription(),
                course.getType().getName(),
                course.getStatus(),
                course.getCreatedBy().getFirstName() + " " + course.getCreatedBy().getLastName(),
                learningUnitUtil.countExercises(course),
                course.getCreatedAt(),
                course.getChildren().stream()
                        .map(this::toChildTreeDto)
                        .toList()
        );
    }

    private LearningUnitTreeDto.ChildDto toChildTreeDto(LearningUnit chapter) {
        return new LearningUnitTreeDto.ChildDto(
                chapter.getId(),
                chapter.getName(),
                chapter.getCode(),
                chapter.getDescription(),
                chapter.getType().getName(),
                chapter.getStatus(),
                chapter.getCreatedAt(),
                chapter.getParent() != null ? chapter.getParent().getId() : null,
                chapter.getChildren().stream()
                        .map(this::toGrandChildTreeDto)
                        .toList()
        );
    }

    private LearningUnitTreeDto.GrandChildDto toGrandChildTreeDto(LearningUnit lesson) {
        return new LearningUnitTreeDto.GrandChildDto(
                lesson.getId(),
                lesson.getName(),
                lesson.getCode(),
                lesson.getDescription(),
                lesson.getType().getName(),
                lesson.getStatus(),
                lesson.getCreatedAt(),
                lesson.getParent() != null ? lesson.getParent().getId() : null
        );
    }
}
