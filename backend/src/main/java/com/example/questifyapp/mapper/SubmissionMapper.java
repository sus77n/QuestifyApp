package com.example.questifyapp.mapper;

import com.example.questifyapp.dto.SubmissionDto;
import com.example.questifyapp.entity.Submission;

public class SubmissionMapper {
    public static SubmissionDto toDto(Submission submission) {
        return new SubmissionDto(
                submission.getId(),
                submission.getExercise().getId(),
                submission.getStudent().getId(),
                submission.getText(),
                submission.getSelectedOption().getId(),
                submission.getSubmittedAt(),
                submission.getScore()
        );
    }

    public static Submission toEntity(SubmissionDto submissionDTO) {
        return new Submission(
                submissionDTO.id(),
                null,
                null,
                submissionDTO.text(),
                submissionDTO.score(),
                submissionDTO.submittedAt(),
                null
        );
    }
}
