package com.example.iquiz.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCompactDto {
    UUID id;
    String name;
    String description;
    String hierarchyPath;
    List<ExerciseCompactDto> exercises;
}
