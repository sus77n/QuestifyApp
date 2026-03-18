package com.example.iquiz.mapper;

import com.example.iquiz.dto.answer.OptionDto;
import com.example.iquiz.dto.exercise.ExerciseResponseDto;
import com.example.iquiz.dto.exercise.ExerciseRequestDto;
import com.example.iquiz.dto.exercise.ExerciseWithAnswerDto;
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
        entity.setType(dto.type());
        entity.setDifficulty(dto.difficulty() != null ? dto.difficulty() : 3);

        return entity;
    }

    public ExerciseWithAnswerDto toDtoWithAnswer(Exercise entity) {
        return new ExerciseWithAnswerDto(
                entity.getId(),
                entity.getQuestion(),
                entity.getType().toString(),
                entity.getDifficulty(),
                entity.getPredefinedAnswers() != null ?
                        answerMapper.toDtoList(entity.getPredefinedAnswers()) : new ArrayList<OptionDto>(),
                ExerciseTypeUtil.removeCorrectAnswerJson(entity.getCorrectAnswerJson()),
                entity.getParent() != null ? entity.getParent().getId() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}