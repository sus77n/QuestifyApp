package com.example.questifyapp.dto;

import com.example.questifyapp.entity.Exercise;

public record ExerciseDTO(
//      Without answer
        Long id,
        String question,
        String type
) {
    public static ExerciseDTO fromEntity(Exercise exercise) {
        return new ExerciseDTO(exercise.getId(), exercise.getQuestion(), exercise.getType());
    }
}
