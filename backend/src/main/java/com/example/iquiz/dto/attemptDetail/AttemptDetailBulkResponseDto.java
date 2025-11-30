package com.example.iquiz.dto.attemptDetail;

public record AttemptDetailBulkResponseDto(
        double averageScore,
        int total,
        int correctCount
) {}
