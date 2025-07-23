package com.example.questifyapp.mapper;

import com.example.questifyapp.dto.submission.SubmissionDto;
import com.example.questifyapp.entity.Submission;
import com.example.questifyapp.repository.ExerciseRepository;
import com.example.questifyapp.repository.OptionRepository;
import com.example.questifyapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubmissionMapper {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OptionRepository optionRepository;

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

        return new Submission(
                dto.id(),
                exerciseRepository.findById(dto.exerciseId())
                        .orElseThrow(() -> new EntityNotFoundException("Exercise with id: " + dto.exerciseId())),
                userRepository.findById(dto.userId())
                        .orElseThrow(() -> new EntityNotFoundException("User with id: " + dto.userId())),
                dto.answer(),
                dto.score(),
                dto.submittedAt(),
                optionRepository.findById(dto.selectedOptionId())
                        .orElseThrow(() -> new EntityNotFoundException("Option with id: " + dto.selectedOptionId()))
        );
    }
}
