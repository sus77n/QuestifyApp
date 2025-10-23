package com.example.iquiz.mapper;

import com.example.iquiz.dto.ExerciseTypeDto;
import com.example.iquiz.entity.ExerciseCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExerciseTypeMapper {
    @Autowired
    private ExerciseMapper exerciseMapper;


    public ExerciseTypeDto toDto(ExerciseCategory exerciseCategory) {
        if (exerciseCategory == null) {
            return null;
        }

        return new ExerciseTypeDto(
                exerciseCategory.getId(),
                exerciseCategory.getCode(),
                exerciseCategory.getExercises().stream().map(exercise -> exerciseMapper.toDto(exercise)).toList()
        );
    }

    public ExerciseCategory toEntity(ExerciseTypeDto exerciseTypeDto) {
        if (exerciseTypeDto == null) {
            return null;
        }
        ExerciseCategory exerciseCategory = new ExerciseCategory();
        exerciseCategory.setId(exerciseCategory.getId());
        exerciseCategory.setCode(exerciseCategory.getCode());
        return exerciseCategory;
    }

}
