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
                exercise.getOptions().stream().map(OptionDTO::fromEntity).toList()
        );
    }
    public static Exercise toEntity(ExerciseDTO exerciseDTO) {
        Exercise exercise = new Exercise();
        exercise.setId(exerciseDTO.id());
        exercise.setQuestion(exerciseDTO.question());
        exercise.setType(exerciseDTO.type());
        exercise.setOptions(
                exerciseDTO.options().stream()
                        .map(OptionDTO::toEntity)
                        .toList()
        );
        return exercise;
    }
}
