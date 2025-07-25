package com.example.iquiz.mapper;

import com.example.iquiz.dto.ExerciseTypeDto;
import com.example.iquiz.entity.ExerciseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExerciseTypeMapper {
    @Autowired
    private ExerciseMapper exerciseMapper;


    public ExerciseTypeDto toDto(ExerciseType exerciseType) {
        if (exerciseType == null) {
            return null;
        }

        return new ExerciseTypeDto(
                exerciseType.getId(),
                exerciseType.getCode(),
                exerciseType.getExercises().stream().map(exercise -> exerciseMapper.toDto(exercise)).toList()
        );
    }

    public ExerciseType toEntity(ExerciseTypeDto exerciseTypeDto) {
        if (exerciseTypeDto == null) {
            return null;
        }
        ExerciseType exerciseType = new ExerciseType();
        exerciseType.setId(exerciseType.getId());
        exerciseType.setCode(exerciseType.getCode());
        return exerciseType;
    }

}
