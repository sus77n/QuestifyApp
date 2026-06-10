package com.example.iquiz.dto.attempt;

import com.example.iquiz.dto.ai.AttemptFeedbackDto;
import com.example.iquiz.dto.attemptDetail.ResultDto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AttemptResponseDto {
    UUID attemptId;
    UUID userId;
    UUID lessonId;
    BigDecimal score;
    String status;
    LocalDateTime submittedAt;
    List<ResultDto> results;
    AttemptFeedbackDto feedback;
}
