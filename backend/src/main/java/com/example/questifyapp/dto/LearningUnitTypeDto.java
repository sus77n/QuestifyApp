package com.example.questifyapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public record LearningUnitTypeDto (
    Long id,
    String type,
    int level
) {
}
