package com.example.iquiz.service;

import com.example.iquiz.dto.submission.ResultDto;
import com.example.iquiz.dto.submission.SubmissionDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.Option;
import com.example.iquiz.entity.Submission;
import com.example.iquiz.mapper.SubmissionMapper;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.OptionRepository;
import com.example.iquiz.repository.SubmissionRepository;
import com.example.iquiz.utility.SubmissionUtil;
import jakarta.transaction.Transactional;
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
    private ExerciseRepository exerciseRepository;
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private SubmissionMapper submissionMapper;
    @Autowired
    private SubmissionUtil submissionUtil;

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
        } else {
            if (exercise.getAnswer().contains(submissionDTO.answer())
                    && (exercise.getAnswer().length() / 2 <= submissionDTO.answer().length())) {
                submission.setScore(BigDecimal.valueOf(100));
            } else {
                submission.setScore(BigDecimal.valueOf(0));
            }
        }

        submissionRepository.save(submission);
        return submissionMapper.toDto(submission);
    }

    @Transactional
    public List<ResultDto> submitAll(List<SubmissionDto> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return new ArrayList<>();
        }
//
//        try {
//            List<Submission> submissions = new ArrayList<>();
//            BigDecimal totalScore = BigDecimal.ZERO;
//
//            for (SubmissionDto submissionDTO : dtoList) {
//                Exercise exercise = exerciseRepository.findById(submissionDTO.exerciseId())
//                        .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + submissionDTO.exerciseId()));
//
//                Option selectedOption = null;
//                if (submissionDTO.selectedOptionId() != null) {
//                    selectedOption = optionRepository.findById(submissionDTO.selectedOptionId())
//                            .orElseThrow(() -> new RuntimeException("Option not found with id: " + submissionDTO.selectedOptionId()));
//                }
//
//                Submission submission = submissionMapper.toEntity(submissionDTO);
//                submission.setSelectedOption(selectedOption);
//
//                BigDecimal submissionScore = submissionUtil.calculateScore(submissionDTO, exercise, selectedOption);
//                submission.setScore(submissionScore);
//                totalScore = totalScore.add(submissionScore);
//
//                submissions.add(submission);
//            }
//
//            submissionRepository.saveAll(submissions);
//            return submissionUtil.calculateAverageScore(totalScore, submissions.size());
//
//        } catch (Exception e) {
//            throw new RuntimeException("Error processing submissions: " + e.getMessage(), e);
//        }

        List<ResultDto> resultDtoList = new ArrayList<>();
        List<Submission> submissions = new ArrayList<>();
        for (SubmissionDto submissionDto : dtoList) {
            Exercise exercise = exerciseRepository.findById(submissionDto.exerciseId())
                    .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + submissionDto.exerciseId()));

            Submission submission = submissionMapper.toEntity(submissionDto);
            BigDecimal submissionScore = BigDecimal.ZERO;
//          Calculate the score based on type of exercise
            if (submissionDto.selectedOptionId() != null) {
                Option selectedOption = optionRepository.findById(submissionDto.selectedOptionId())
                        .orElseThrow(() -> new RuntimeException("Option not found with id: " + submissionDto.selectedOptionId()));
                submission.setSelectedOption(selectedOption);
                submissionScore = submissionUtil.calculateScore(submissionDto, exercise, selectedOption);
            } else if (submissionDto.answer() != null) {
                submission.setAnswer(submissionDto.answer());
                submissionScore = submissionUtil.calculateScore(submissionDto, exercise, null);
            }
            submission.setScore(submissionScore);
            submissions.add(submission);
            resultDtoList.add(submissionMapper.entityToResultDto(submission));
        }
        submissionRepository.saveAll(submissions);
        return resultDtoList;
    }

    public SubmissionDto getSubmissionByUserIdAndExerciseId(Long userId, Long exerciseId) {
        Submission submission = submissionRepository.findTopByUserIdAndExerciseIdOrderBySubmittedAtDesc(userId, exerciseId).orElse(null);
        return submissionMapper.toDto(submission);
    }
}
