package com.example.iquiz.dto.submission;

public record SubmissionBulkResponseDto(
        double averageScore,
        int total,
        int correctCount
) {}
