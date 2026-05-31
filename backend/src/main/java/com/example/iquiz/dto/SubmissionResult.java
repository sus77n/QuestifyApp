package com.example.iquiz.dto;

import com.example.iquiz.dto.ai.AttemptFeedbackDto;
import com.example.iquiz.dto.attemptDetail.ResultDto;
import com.example.iquiz.entity.AttemptDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SubmissionResult {

    private List<AttemptDetail> details;
    private List<ResultDto> results;
    private Map<UUID, int[]> categoryStats;
    private BigDecimal finalScore;
    private int correctCount;
    private int wrongCount;
    private AttemptFeedbackDto feedback;
}