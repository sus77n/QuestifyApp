package com.example.iquiz.mapper;

import com.example.iquiz.dto.attemptDetail.AttemptDetailDto;
import com.example.iquiz.entity.AttemptDetail;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttemptDetailMapper {

    private final ExerciseRepository exerciseRepository;

    public AttemptDetailDto toDto(AttemptDetail attemptDetail) {
        if (attemptDetail == null) {
            return null;
        }

        return new AttemptDetailDto(
                attemptDetail.getId(),
                attemptDetail.getExercise().getId(),
                attemptDetail.getUserAnswerJson(),
                attemptDetail.getScore()
        );
    }

    public AttemptDetail toEntity(AttemptDetailDto dto) {
        if (dto == null) {
            return null;
        }

        AttemptDetail entity = new AttemptDetail();

        entity.setExercise(exerciseRepository.findById(dto.exerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", dto.exerciseId())));
        entity.setUserAnswerJson(dto.userAnswerJson());

        return entity;
    }

}

