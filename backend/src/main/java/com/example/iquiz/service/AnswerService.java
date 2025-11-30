package com.example.iquiz.service;
import com.example.iquiz.dto.answer.AnswerRequestDto;
import com.example.iquiz.dto.answer.OptionDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.Answer;
import com.example.iquiz.mapper.AnswerMapper;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final ExerciseRepository exerciseRepository;
    private final AnswerMapper answerMapper;

    public List<OptionDto> getAllOptions() {
        return answerRepository.findAll().stream()
                .map(answerMapper::toOptionDto)
                .toList();
    }

    public OptionDto getOptionById(UUID id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Option not found with id: " + id));
        return answerMapper.toOptionDto(answer);
    }

    public List<OptionDto> getOptionsByExerciseId(UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NullPointerException("Exercise not found with id: " + exerciseId));
        return exercise.getPredefinedAnswers().stream()
                .map(answerMapper::toOptionDto)
                .toList();
    }

    @Transactional
    public OptionDto saveOption(AnswerRequestDto dto) {
        Exercise exercise = exerciseRepository.findById(dto.exerciseId())
                .orElseThrow(() -> new NullPointerException("Exercise not found with id: " + dto.exerciseId()));

        Answer answer = answerMapper.toEntity(dto);
        answer.setExercise(exercise);

        answerRepository.save(answer);
        return answerMapper.toOptionDto(answer);
    }

    @Transactional
    public OptionDto updateOption(UUID id, AnswerRequestDto dto) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Option not found with id: " + id));

        if (dto.exerciseId() != null) {
            Exercise exercise = exerciseRepository.findById(dto.exerciseId())
                    .orElseThrow(() -> new NullPointerException("Exercise not found with id: " + dto.exerciseId()));
            answer.setExercise(exercise);
        }

        answer.setText(dto.text());
        answer.setHeader(dto.header());

        answerRepository.save(answer);
        return answerMapper.toOptionDto(answer);
    }

    public void deleteOptionById(UUID id) {
        answerRepository.deleteById(id);
    }
}
