package com.example.iquiz.service;

import com.example.iquiz.dto.submission.SubmissionBulkResponseDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ExerciseRepository exerciseRepository;
    private final OptionRepository optionRepository;
    private final SubmissionMapper submissionMapper;
    private final SubmissionUtil submissionUtil;

    public SubmissionDto submit(SubmissionDto submissionDTO) {
        Exercise exercise = exerciseRepository.findById(submissionDTO.exerciseId())
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        Submission submission = submissionMapper.toEntity(submissionDTO);

        if (submissionDTO.selectedOptionId() != null) {
            Option option = optionRepository.findById(submissionDTO.selectedOptionId())
                    .orElseThrow(() -> new RuntimeException("Option not found"));
            submission.setSelectedOption(option);
            submission.setScore(option.isCorrect() ? BigDecimal.valueOf(100) : BigDecimal.ZERO);
        } else if (submissionDTO.answer() != null) {
            submission.setScore(
                    submissionUtil.calculateScore(submissionDTO, exercise, null)
            );
        }

        submissionRepository.save(submission);
        return submissionMapper.toDto(submission);
    }

    @Transactional
    public SubmissionBulkResponseDto submitAll(List<SubmissionDto> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return new SubmissionBulkResponseDto(0.0, 0, 0);
        }

        List<Submission> submissions = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;
        int correctCount = 0;

        for (SubmissionDto dto : dtoList) {
            Exercise exercise = exerciseRepository.findById(dto.exerciseId())
                    .orElseThrow(() -> new RuntimeException("Exercise not found"));

            Option selectedOption = dto.selectedOptionId() != null
                    ? optionRepository.findById(dto.selectedOptionId())
                    .orElseThrow(() -> new RuntimeException("Option not found"))
                    : null;

            Submission submission = submissionMapper.toEntity(dto);
            submission.setSelectedOption(selectedOption);

            BigDecimal score = submissionUtil.calculateScore(dto, exercise, selectedOption);
            submission.setScore(score);
            totalScore = totalScore.add(score);

            if (score.compareTo(BigDecimal.ZERO) > 0) {
                correctCount++;
            }

            submissions.add(submission);
        }

        submissionRepository.saveAll(submissions);

        double avg = submissionUtil.calculateAverageScore(totalScore, submissions.size());
        return new SubmissionBulkResponseDto(avg, submissions.size(), correctCount);
    }


    public SubmissionDto getSubmissionByUserIdAndExerciseId(Long userId, Long exerciseId) {
        return submissionRepository.findTopByUserIdAndExerciseIdOrderBySubmittedAtDesc(userId, exerciseId)
                .map(submissionMapper::toDto)
                .orElse(null);
    }
}

