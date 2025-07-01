package com.example.questifyapp.service;

import com.example.questifyapp.dto.SubmissionDTO;
import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.entity.Option;
import com.example.questifyapp.entity.Submission;
import com.example.questifyapp.entity.User;
import com.example.questifyapp.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private OptionService optionService;
    @Autowired
    private AuthService authService;
    @Autowired
    private ExerciseService exerciseService;

    public BigDecimal gradingSubmissionDTO(SubmissionDTO submission) {
        if (submission.optionId() == null) {
            return BigDecimal.valueOf(50);
        }

        Option option = optionService.getOptionById(submission.optionId());
        if (option.isCorrect()) {
            return BigDecimal.valueOf(100);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public Submission saveOrUpdateSubmission(SubmissionDTO submissionDTO, BigDecimal score) {
        Optional<Submission> existingSubmission =
                submissionRepository.findByStudentIdAndExerciseId(submissionDTO.userId(), submissionDTO.exerciseId());

        Submission submission;
        if (existingSubmission.isPresent()) {
            submission = existingSubmission.get();
            submission.setSubmission(submissionDTO.text());
            submission.setScore(gradingSubmissionDTO(submissionDTO));
        } else {
            Exercise exercise = exerciseService.getExerciseById(submissionDTO.exerciseId());
            User user = authService.getUserById(submissionDTO.userId());
            submission = new Submission(
                    exercise,
                    user,
                    submissionDTO.text(),
                    score
            );
        }

        return submissionRepository.save(submission);
    }
}
