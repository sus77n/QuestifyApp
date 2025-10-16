package com.example.iquiz.mapper;

import com.example.iquiz.dto.submission.SubmissionDto;
import com.example.iquiz.entity.Submission;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.OptionRepository;
import com.example.iquiz.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubmissionMapper {

    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;
    private final OptionRepository optionRepository;

    public SubmissionDto toDto(Submission submission) {
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

    public Submission toEntity(SubmissionDto dto) {
        if (dto == null) {
            return null;
        }

        Submission entity = new Submission();

        entity.setExercise(exerciseRepository.findById(dto.exerciseId())
                .orElseThrow(() -> new EntityNotFoundException("Exercise with id: " + dto.exerciseId())));
        entity.setUser(userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + dto.userId())));
        entity.setAnswer(dto.answer());

        if (dto.selectedOptionId() != null) {
            entity.setSelectedOption(optionRepository.findById(dto.selectedOptionId())
                    .orElseThrow(() -> new EntityNotFoundException("Option with id: " + dto.selectedOptionId())));
        }

        return entity;
    }
}

