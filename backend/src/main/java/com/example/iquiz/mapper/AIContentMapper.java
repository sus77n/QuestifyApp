package com.example.iquiz.mapper;

import com.example.iquiz.dto.ai.CategoryCompactDto;
import com.example.iquiz.dto.ai.ExerciseCompactDto;
import com.example.iquiz.dto.ai.ExportedCategoryDto;
import com.example.iquiz.entity.Answer;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.enums.ExerciseType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
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
        if (dto.metadata() != null) {
            try {
                String metadataJson = objectMapper.writeValueAsString(dto.metadata());
                answer.setMetadata(metadataJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize metadata", e);
            }
        }

        return answer;
    }

    public CategoryCompactDto toCategoryCompactDto(LearningUnit category) {
        if (category == null) return null;

        return CategoryCompactDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .hierarchyPath(buildHierarchyPath(category))
                .exercises(toExerciseCompactList(category.getExercises()))
                .build();
    }

    public List<CategoryCompactDto> toCategoryCompactList(List<LearningUnit> categories) {
        if (categories == null) return List.of();

        return categories.stream()
                .map(this::toCategoryCompactDto)
                .toList();
    }

    public ExerciseCompactDto toExerciseCompactDto(Exercise ex) {
        if (ex == null) return null;

        return ExerciseCompactDto.builder()
                .id(ex.getId())
                .type(ex.getType() != null ? ex.getType().name() : null)
                .difficulty(ex.getDifficulty())
                .question(cleanText(ex.getQuestion()))
                .correctAnswer(cleanJson(ex.getCorrectAnswerJson()))
                .build();
    }

    public List<ExerciseCompactDto> toExerciseCompactList(List<Exercise> exercises) {
        if (exercises == null) return new ArrayList<>();

        return exercises.stream()
                .map(this::toExerciseCompactDto)
                .toList();
    }

    public String buildHierarchyPath(LearningUnit unit) {
        List<String> nodes = new ArrayList<>();
        LearningUnit current = unit;

        while (current != null) {
            nodes.add(current.getType().getName() + ": " + current.getName());
            current = current.getParent();
        }

        Collections.reverse(nodes);
        return String.join(" > ", nodes);
    }

    private String cleanText(String input) {
        return input == null ? "" : input.replace("\n", " ").trim();
    }

    private String cleanJson(String input) {
        return input == null ? "" : input.replace("\n", "").replace(" ", "").trim();
    }
}