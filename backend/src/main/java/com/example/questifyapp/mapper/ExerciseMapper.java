package com.example.questifyapp.mapper;

import com.example.questifyapp.dto.exercise.ExerciseResponseDto;
import com.example.questifyapp.dto.exercise.ExerciseRequestDto;
import com.example.questifyapp.entity.Exercise;

import java.util.ArrayList;

public class ExerciseMapper {

    public static ExerciseResponseDto toDto(Exercise entity) {
        return new ExerciseResponseDto(
                entity.getId(),
                entity.getQuestion(),
                entity.getType(),
                new ArrayList<>()
        );
    }

    public static Exercise toEntity(ExerciseRequestDto dto) {
        return new Exercise(
                dto.id(),
                dto.question(),
                dto.answer(),
                dto.type(),
                null,
                null,
                null,
                null
        );
    }
}
