package com.example.questifyapp.mapper;

import com.example.questifyapp.dto.ExerciseDTO;
import com.example.questifyapp.dto.OptionDTO;
import com.example.questifyapp.entity.Exercise;

public class ExerciseMapper {

    public static ExerciseDTO toDto(Exercise exercise) {
        return new ExerciseDTO(
                exercise.getId(),
                exercise.getQuestion(),
                exercise.getType(),
                exercise.getOptions().stream()
                        .map(OptionMapper::toDto)
                        .toList(),
                exercise.getCreatedAt(),
                exercise.getUpdatedAt(),
                LearningUnitMapper.toDto(exercise.getParentUnit())
        );
    }

    public static Exercise toEntity(ExerciseDTO exerciseDTO) {
        return new Exercise(
                exerciseDTO.id(),
                exerciseDTO.question(),
                null,
                exerciseDTO.type(),
                exerciseDTO.createdAt(),
                exerciseDTO.updatedAt(),
                LearningUnitMapper.toEntity(exerciseDTO.parentUnit()),
                exerciseDTO.options().stream()
                        .map(OptionMapper::toEntity)
                        .toList()
        );
    }
}
