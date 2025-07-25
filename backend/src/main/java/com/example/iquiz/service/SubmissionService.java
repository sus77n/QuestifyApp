package com.example.iquiz.service;

import com.example.iquiz.dto.submission.SubmissionDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.Option;
import com.example.iquiz.entity.Submission;
import com.example.iquiz.mapper.SubmissionMapper;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.OptionRepository;
import com.example.iquiz.repository.SubmissionRepository;
import com.example.iquiz.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private SubmissionMapper submissionMapper;

    public SubmissionDto submit(SubmissionDto submissionDTO) {

        Exercise exercise = exerciseRepository.findById(submissionDTO.exerciseId())
                .orElseThrow(() -> new NullPointerException("exercise not found"));

        Submission submission = submissionMapper.toEntity(submissionDTO);

        if (submissionDTO.selectedOptionId() != 0) {
            Option option = optionRepository.findById(submissionDTO.selectedOptionId()).orElse(null);
            submission.setSelectedOption(option);
            if (option.isCorrect()) {
                submission.setScore(BigDecimal.valueOf(100));
            } else {
                submission.setScore(BigDecimal.valueOf(0));
            }
        } else  {
            if (exercise.getAnswer().contains(submissionDTO.answer())
                    && (exercise.getAnswer().length()/2 <= submissionDTO.answer().length())) {
                submission.setScore(BigDecimal.valueOf(100));
            } else  {
                submission.setScore(BigDecimal.valueOf(0));
            }
        }

        submissionRepository.save(submission);
        return submissionMapper.toDto(submission);
    }

    public Double submitAll(List<SubmissionDto> dtoList) {
        List<Submission> submissions = new ArrayList<>();
        BigDecimal score = BigDecimal.ZERO;

        for (SubmissionDto submissionDTO : dtoList) {
        Exercise exercise = exerciseRepository.findById(submissionDTO.exerciseId())
                .orElseThrow(() -> new NullPointerException("exercise not found"));

        Submission submission = submissionMapper.toEntity(submissionDTO);

        if (submissionDTO.selectedOptionId() != 0) {
            Option option = optionRepository.findById(submissionDTO.selectedOptionId()).orElse(null);
            submission.setSelectedOption(option);
            if (option.isCorrect()) {
                submission.setScore(BigDecimal.valueOf(100));
            } else {
                submission.setScore(BigDecimal.valueOf(0));
            }
        } else  {
            if (exercise.getAnswer().contains(submissionDTO.answer())
                    && (exercise.getAnswer().length()/2 <= submissionDTO.answer().length())) {
                submission.setScore(BigDecimal.valueOf(100));
            } else  {
                submission.setScore(BigDecimal.valueOf(0));
            }
        }
        score.add(submission.getScore());
        submissions.add(submission);
        }

        submissionRepository.saveAll(submissions);
        return score.doubleValue()/submissions.size();
    }

    public SubmissionDto getSubmissionByUserIdAndExerciseId(Long userId, Long exerciseId) {
        Submission submission = submissionRepository.findTopByUserIdAndExerciseIdOrderBySubmittedAtDesc(userId, exerciseId).orElse(null);
        return submissionMapper.toDto(submission);
    }
}
