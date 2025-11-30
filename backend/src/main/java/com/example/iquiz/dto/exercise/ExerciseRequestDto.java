package com.example.iquiz.dto.exercise;

import com.example.iquiz.dto.answer.AnswerRequestDto;

import java.util.List;
import java.util.UUID;

public record ExerciseRequestDto(
        String question,
        String type,
        String answer,  // JSON string with correct answers
        int difficulty,
        List<AnswerRequestDto> options,
        UUID parentUnitId
) {
    public boolean isValid() {
        return question != null && !question.trim().isEmpty() &&
                type != null && !type.trim().isEmpty() &&
                answer != null && !answer.trim().isEmpty() &&
                difficulty >= 1 && difficulty <= 10 &&
                parentUnitId != null;
    }
}