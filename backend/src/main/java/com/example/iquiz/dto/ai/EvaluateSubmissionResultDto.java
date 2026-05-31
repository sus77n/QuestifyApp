package com.example.iquiz.dto.ai;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EvaluateSubmissionResultDto {
    UUID exerciseId;
    BigDecimal score;
    String feedback;
}
