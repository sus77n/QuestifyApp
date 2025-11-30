package com.example.iquiz.service;

import com.example.iquiz.dto.attemptDetail.AttemptDetailBulkResponseDto;
import com.example.iquiz.dto.attemptDetail.AttemptDetailDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.AttemptDetail;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.mapper.AttemptDetailMapper;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.AttemptDetailRepository;
import com.example.iquiz.utility.SubmissionUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttemptDetailService {

    private final AttemptDetailRepository attemptDetailRepository;
    private final ExerciseRepository exerciseRepository;
    private final AttemptDetailMapper attemptDetailMapper;
    private final SubmissionUtil submissionUtil;

    @Transactional
    public AttemptDetailDto submit(AttemptDetailDto dto) {
        Exercise exercise = exerciseRepository.findById(dto.exerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", dto.exerciseId()));

        AttemptDetail attemptDetail = attemptDetailMapper.toEntity(dto);

        // calculate score from JSON answers
        BigDecimal score = submissionUtil.calculateScore(
                exercise,
                dto.userAnswerJson()
        );

        attemptDetail.setScore(score);
        attemptDetail.setExercise(exercise);
        attemptDetailRepository.save(attemptDetail);

        return attemptDetailMapper.toDto(attemptDetail);
    }

    @Transactional
    public AttemptDetailBulkResponseDto submitAll(List<AttemptDetailDto> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return new AttemptDetailBulkResponseDto(0.0, 0, 0);
        }

        List<AttemptDetail> attemptDetails = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;
        int correctCount = 0;

        for (AttemptDetailDto dto : dtoList) {
            Exercise exercise = exerciseRepository.findById(dto.exerciseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", dto.exerciseId()));

            AttemptDetail attemptDetail = attemptDetailMapper.toEntity(dto);
            attemptDetail.setExercise(exercise);

            BigDecimal score = submissionUtil.calculateScore(
                    exercise,
                    dto.userAnswerJson()
            );

            attemptDetail.setScore(score);
            attemptDetails.add(attemptDetail);
            totalScore = totalScore.add(score);

            if (score.compareTo(BigDecimal.ZERO) > 0) {
                correctCount++;
            }
        }

        attemptDetailRepository.saveAll(attemptDetails);

        double avgScore = submissionUtil.calculateAverageScore(totalScore, attemptDetails.size());
        return new AttemptDetailBulkResponseDto(avgScore, attemptDetails.size(), correctCount);
    }
}
