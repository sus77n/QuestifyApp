package com.example.questifyapp.dto;

import com.example.questifyapp.entity.Exercise;

import java.util.List;

public record ExerciseDTO(
//      Without answer
        Long id,
        String question,
        String type,
        List<OptionDTO> options
) {
    
}

