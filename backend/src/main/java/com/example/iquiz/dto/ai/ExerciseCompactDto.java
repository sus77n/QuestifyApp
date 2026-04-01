package com.example.iquiz.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseCompactDto {

    private UUID id;

    private String type;

    private String question;

    private String correctAnswer;

    private String explanation;

    private int difficulty;
}