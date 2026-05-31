package com.example.iquiz.dto.ai;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EvaluateSubmissionItemDto {
    UUID exerciseId;
    String type;
    String question;
    String expectedAnswer;
    String userAnswer;
}
