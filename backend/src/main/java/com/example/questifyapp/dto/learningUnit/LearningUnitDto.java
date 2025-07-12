package com.example.questifyapp.dto.learningUnit;

import com.example.questifyapp.dto.LearningUnitTypeDto;
import com.example.questifyapp.dto.UserDto;
import com.example.questifyapp.entity.LearningUnitType;

import java.time.LocalDateTime;
import java.util.List;
public record LearningUnitDto(
        Long id,
        String name,
        String code,
        String description,
        LearningUnitTypeDto type,
        int status,
        LocalDateTime createdAt,
        UserDto createdBy,
        Long parentId,
        List<LearningUnitChildDto> childUnits
) {}
