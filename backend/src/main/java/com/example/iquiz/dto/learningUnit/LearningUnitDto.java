package com.example.iquiz.dto.learningUnit;

import com.example.iquiz.dto.UserDto;
import com.example.iquiz.dto.exercise.ExerciseResponseDto;

import java.time.LocalDateTime;
import java.util.List;
public record LearningUnitDto(
        Long id,
        String name,
        String code,
        String description,
        String type,
        int status,
        LocalDateTime createdAt,
        UserDto createdBy,
        Long parentId,
        List<LearningUnitChildDto> childUnits,
        List<ExerciseResponseDto> exercises,
        Long numberOfExercise
) {}
