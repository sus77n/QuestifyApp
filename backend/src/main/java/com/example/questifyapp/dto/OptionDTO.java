package com.example.questifyapp.dto;

import com.example.questifyapp.entity.Option;

public record OptionDTO(
        Long id,
        String text,
        ExerciseDTO exercise
) {}