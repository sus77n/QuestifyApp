package com.example.questifyapp.dto.learningUnit;

import com.example.questifyapp.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public record LearningUnitChildDto(
        Long id,
        String name,
        String type
) {
}
