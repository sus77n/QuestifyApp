package com.example.questifyapp.mapper;

import com.example.questifyapp.dto.SubmissionDTO;
import com.example.questifyapp.entity.Submission;

public class SubmissionMapper {
    public static SubmissionDTO toDto(Submission submission) {
        return new SubmissionDTO(
                submission.getId(),
                submission.getStudent().getId(),
                submission.getSubmission(),
                submission.getSelectedOption().getId()
        );
    }

    public static Submission toEntity(SubmissionDTO submissionDTO) {
        return new Submission(
                submissionDTO.exerciseId(),
                submissionDTO.userId(),
                submissionDTO.submission(),
                submissionDTO.optionId(), 
                
        );
    }
}
