package com.example.iquiz.mapper;

import com.example.iquiz.dto.ai.ExportedCategoryDto;
import com.example.iquiz.entity.Answer;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.enums.ExerciseType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AIContentMapper {

    private final ObjectMapper objectMapper;

    public Exercise toExercise(ExportedCategoryDto.GeneratedExerciseDto dto) {
        Exercise exercise = new Exercise();
        exercise.setQuestion(dto.question());

        try {
            exercise.setType(ExerciseType.valueOf(dto.type()));
        } catch (IllegalArgumentException | NullPointerException e) {
        exercise.setType(ExerciseType.UNDEFINED);
        }

        exercise.setDifficulty(dto.difficulty());

        try {
            if (dto.correctAnswerJson() != null && dto.correctAnswerJson().correctAnswers() != null) {
                String jsonString = objectMapper.writeValueAsString(dto.correctAnswerJson().correctAnswers());
                exercise.setCorrectAnswerJson(jsonString);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize correct answers logic", e);
        }

        if (dto.options() != null) {
            List<Answer> answers = dto.options().stream()
                    .map(this::toAnswer)
                    .peek(ans -> ans.setExercise(exercise))
                    .toList();
            exercise.setPredefinedAnswers(new ArrayList<>(answers));
        }

        return exercise;
    }

    public Answer toAnswer(ExportedCategoryDto.GeneratedOptionDto dto) {
        Answer answer = new Answer();
        answer.setText(dto.text());
        answer.setHeader(dto.header());
        answer.setMetadata(dto.metadata());
        return answer;
    }
}