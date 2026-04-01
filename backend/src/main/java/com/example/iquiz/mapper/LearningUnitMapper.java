package com.example.iquiz.mapper;

import com.example.iquiz.dto.learningUnit.*;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.repository.LearningUnitRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
)
public abstract class LearningUnitMapper {

    @Autowired
    protected LearningUnitRepository learningUnitRepository;

    @Mapping(target = "type", source = "type.name")
    @Mapping(target = "createdBy", expression = "java(fullName(unit))")
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "numberOfExercise", ignore = true)
    public abstract LearningUnitDto toDtoBase(LearningUnit unit);

    public LearningUnitDto toDto(LearningUnit unit) {

        if (unit == null) {
            return null;
        }

        LearningUnitDto dto = toDtoBase(unit);

        List<LearningUnitDto> children = new ArrayList<>();

        if (unit.getChildren() != null) {
            for (LearningUnit child : unit.getChildren()) {
                children.add(toDto(child));
            }
        }

        return new LearningUnitDto(
                dto.id(),
                dto.name(),
                dto.code(),
                dto.description(),
                dto.type(),
                dto.status(),
                dto.createdAt(),
                dto.createdBy(),
                dto.parentId(),
                children,
                0
        );
    }

    public LearningUnitDto toDtoWithoutCategory(LearningUnit unit) {

        if (unit == null) {
            return null;
        }

        LearningUnitDto dto = toDtoBase(unit);

        List<LearningUnitDto> children;

        if (unit.getChildren() == null
                || (!unit.getChildren().isEmpty()
                && unit.getChildren().get(0).getType().getName()
                .equalsIgnoreCase("Exercise Category"))) {

            children = new ArrayList<>();

        } else {

            children = unit.getChildren()
                    .stream()
                    .map(this::toDtoWithoutCategory)
                    .toList();
        }

        return new LearningUnitDto(
                dto.id(),
                dto.name(),
                dto.code(),
                dto.description(),
                dto.type(),
                dto.status(),
                dto.createdAt(),
                dto.createdBy(),
                dto.parentId(),
                children,
                0
        );
    }

    @Mapping(target = "type", source = "type.name")
    @Mapping(target = "createdBy", expression = "java(fullName(unit))")
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "numberOfComplete", ignore = true)
    @Mapping(target = "numberOfExercise", ignore = true)
    public abstract LearningUnitWithStatisticDto toStatisticBase(LearningUnit unit);

    public LearningUnitWithStatisticDto toDtoWithStatistic(LearningUnit unit, UUID userId) {

        if (unit == null) {
            return null;
        }

        LearningUnitWithStatisticDto base = toStatisticBase(unit);

        List<LearningUnitWithStatisticDto> children;

        if (unit.getChildren() == null
                || (!unit.getChildren().isEmpty()
                && unit.getChildren().get(0).getType().getName()
                .equalsIgnoreCase("Exercise Category"))) {

            children = new ArrayList<>();

        } else {

            children = unit.getChildren()
                    .stream()
                    .map(child -> toDtoWithStatistic(child, userId))
                    .toList();
        }

        long numberOfExercise;
        long numberOfComplete;

        if (unit.getLessonConfig() != null) {

            numberOfExercise = unit.getLessonConfig().getQuestionsPerAttempt();
            numberOfComplete = learningUnitRepository.countPassedExercisesInBestAttempt(unit.getId(), userId);

        } else {

            numberOfExercise = children.stream()
                    .mapToLong(LearningUnitWithStatisticDto::getNumberOfExercise)
                    .sum();

            numberOfComplete = children.stream()
                    .mapToLong(LearningUnitWithStatisticDto::getNumberOfComplete)
                    .sum();
        }

        return new LearningUnitWithStatisticDto(
                base.getId(),
                base.getName(),
                base.getCode(),
                base.getDescription(),
                base.getType(),
                base.getStatus(),
                base.getCreatedAt(),
                base.getCreatedBy(),
                base.getParentId(),
                children,
                numberOfComplete < numberOfExercise ? numberOfComplete : numberOfExercise,
                numberOfExercise
        );
    }

    @Mapping(target = "type", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    public abstract LearningUnit toEntity(LearningUnitDto dto);

    @Mapping(target = "createdBy", expression = "java(fullName(entity))")
    public abstract CourseDto toCourseDto(LearningUnit entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract LearningUnit courseDtoToEntity(CourseDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", constant = "1")
    public abstract LearningUnit generatedCategoryToEntity(CreateExerciseCategoryDto dto);

    @Mapping(target = "type", source = "type.name")
    @Mapping(target = "createdBy", expression = "java(fullName(unit))")
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "exerciseCategories", ignore = true)
    @Mapping(target = "lessonConfig", source = "lessonConfig")
    public abstract LessonDetailDto toLessonDetailBase(LearningUnit unit);

    public LessonDetailDto toLessonDetailDto(LearningUnit unit) {

        if (unit == null) {
            return null;
        }

        LessonDetailDto base = toLessonDetailBase(unit);

        List<LearningUnitDto> children = new ArrayList<>();

        if (unit.getChildren() != null) {
            for (LearningUnit child : unit.getChildren()) {
                children.add(toDto(child));
            }
        }

        return new LessonDetailDto(
                base.id(),
                base.name(),
                base.code(),
                base.description(),
                base.type(),
                base.status(),
                base.createdAt(),
                base.createdBy(),
                base.parentId(),
                children,
                base.lessonConfig()
        );
    }

    public String fullName(LearningUnit unit) {

        if (unit.getCreatedBy() == null) {
            return null;
        }

        return unit.getCreatedBy().getFirstName()
                + " "
                + unit.getCreatedBy().getLastName();
    }

    public CreateExerciseCategoryDto toCreateExerciseCategoryDtoForGenerateExercise(LearningUnit unit) {

        if (unit == null) {
            return null;
        }

        return new CreateExerciseCategoryDto(
                unit.getName(),
                unit.getCode(),
                unit.getType().getName(),
                unit.getDescription(),
                null
        );
    }
}