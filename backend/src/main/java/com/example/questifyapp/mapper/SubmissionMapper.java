package com.example.questifyapp.mapper;

import com.example.questifyapp.dto.SubmissionDto;
import com.example.questifyapp.entity.Submission;

public class SubmissionMapper {
    public static SubmissionDto toDto(Submission submission) {

        if (submission == null) {
            return null;
        }

        return new SubmissionDto(
                submission.getId(),
                submission.getExercise().getId(),
                submission.getUser().getId(),
                submission.getAnswer(),
                submission.getSelectedOption() != null ? submission.getSelectedOption().getId() : null,
                submission.getSubmittedAt(),
                submission.getScore()
        );
    }

    public static Submission toEntity(SubmissionDto submissionDTO) {

        if (submissionDTO == null) {
            return null;
        }

        return new Submission(
                submissionDTO.id(),
                null,
                null,
                submissionDTO.answer(),
                submissionDTO.score(),
                submissionDTO.submittedAt(),
                null
        );
    }
}
