package com.example.iquiz.dto;

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

    private List<AttemptDetail> details;      // for DB save
    private List<ResultDto> feedbacks;        // for API response
    private Map<UUID, int[]> categoryStats;   // for mastery update
    private BigDecimal finalScore;            // for Attempt
}