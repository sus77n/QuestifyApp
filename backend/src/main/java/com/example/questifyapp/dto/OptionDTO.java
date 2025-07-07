package com.example.questifyapp.dto;

public record OptionDTO(
        Long id,
        String text,
        boolean isCorrect,
        ExerciseDTO exercise
) {}