package com.example.questifyapp.mapper;

import com.example.questifyapp.dto.SubmissionDTO;
import com.example.questifyapp.entity.Submission;

public class SubmissionMapper {
    public static SubmissionDTO toDto(Submission submission) {
        return new SubmissionDTO(
                submission.getId(),
                ExerciseMapper.toDto(submission.getExercise()),
                submission.getStudent().getId(),
                submission.getText(),
                OptionMapper.toDto(submission.getSelectedOption()),
                submission.getSubmittedAt(),
                submission.getScore()
        );
    }

    public static Submission toEntity(SubmissionDTO submissionDTO) {
        return new Submission(
                submissionDTO.id(),
                ExerciseMapper.toEntity(submissionDTO.exercise()),
                null,
                submissionDTO.text(),
                submissionDTO.score(),
                submissionDTO.submittedAt(),
                OptionMapper.toEntity(submissionDTO.selectedOption())
        );
    }
}
