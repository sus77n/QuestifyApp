package com.example.iquiz.mapper;

import com.example.iquiz.dto.answer.OptionDto;
import com.example.iquiz.dto.exercise.ExerciseResponseDto;
import com.example.iquiz.dto.exercise.ExerciseRequestDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.enums.ExerciseType;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.repository.LearningUnitRepository;
import com.example.iquiz.utility.ExerciseTypeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ExerciseMapper {
    @Autowired
    private LearningUnitRepository learningUnitRepository;
    @Autowired
    private AnswerMapper answerMapper;

    public ExerciseResponseDto toDto(Exercise entity) {
        return new ExerciseResponseDto(
                entity.getId(),
                entity.getQuestion(),
                entity.getType().toString(),
                entity.getPredefinedAnswers() != null ?
                        answerMapper.toDtoList(entity.getPredefinedAnswers()) : new ArrayList<OptionDto>()
        );
    }

    public Exercise toEntity(ExerciseRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Exercise entity = new Exercise();
        entity.setQuestion(dto.question());
        entity.setType(ExerciseType.valueOf(dto.type()));
        entity.setCorrectAnswerJson(dto.answer());
        entity.setDifficulty(dto.difficulty());

        // Set predefined answers only if provided and appropriate for the exercise type
        if (dto.options() != null && !dto.options().isEmpty()) {
            entity.setPredefinedAnswers(dto.options().stream()
                    .map(answerMapper::toEntity)
                    .peek(op -> op.setExercise(entity))
                    .toList());
        } else {
            entity.setPredefinedAnswers(new ArrayList<>());
        }

        entity.setParent(learningUnitRepository.findById(dto.parentUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Learning unit", "id", dto.parentUnitId())));

        return entity;
    }
}