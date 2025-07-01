package com.example.questifyapp.dto;

public record SubmissionDTO(
        Long exerciseId,
        Long userId,
        String text,
        Long optionId
) {
}
