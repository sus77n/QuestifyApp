package com.example.iquiz.mapper;

import com.example.iquiz.dto.exercise.ExerciseResponseDto;
import com.example.iquiz.dto.exercise.ExerciseRequestDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.repository.LearningUnitRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ExerciseMapper {
    @Autowired
    private LearningUnitRepository learningUnitRepository;

    public ExerciseResponseDto toDto(Exercise entity) {
        return new ExerciseResponseDto(
                entity.getId(),
                entity.getQuestion(),
                entity.getType(),
                new ArrayList<>()
        );
    }

    public Exercise toEntity(ExerciseRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Exercise entity = new Exercise();
        entity.setQuestion(dto.question());
        entity.setType(dto.type());
        entity.setAnswer(dto.answer());
        entity.setDifficulty(dto.difficulty());
        entity.setParent(learningUnitRepository.findById(dto.parentUnitId())
                .orElseThrow(() -> new EntityNotFoundException("Parent Unit with id: " + dto.parentUnitId())));

        return entity;
    }
}
