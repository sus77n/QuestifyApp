package com.example.questifyapp.dto;

import com.example.questifyapp.entity.Exercise;

import java.util.List;

public record ExerciseDTO(
//      Without answer
        Long id,
        String question,
        String type,
        List<OptionDTO> options
) {
    public static ExerciseDTO fromEntity(Exercise exercise) {
        return new ExerciseDTO(
                exercise.getId(),
                exercise.getQuestion(),
                exercise.getType(),
                exercise.getOptions().stream().map(OptionDTO::fromEntity).toList()
        );
    }
}
