package com.example.iquiz.service;

import com.example.iquiz.dto.exercise.ExerciseRequestDto;
import com.example.iquiz.dto.exercise.ExerciseResponseDto;
import com.example.iquiz.dto.answer.OptionDto;
import com.example.iquiz.entity.Answer;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.enums.ExerciseType;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.mapper.ExerciseMapper;
import com.example.iquiz.mapper.AnswerMapper;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.LearningUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExerciseService {
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private LearningUnitRepository learningUnitRepository;
    @Autowired
    private ExerciseMapper exerciseMapper;
    @Autowired
    private AnswerMapper answerMapper;

    public List<ExerciseResponseDto> getAllExercises() {
        return exerciseRepository.findAll()
                .stream().map(exerciseMapper::toDto).collect(Collectors.toList());
    }

    public ExerciseResponseDto getExerciseById(UUID exerciseId) {
        return exerciseMapper.toDto(exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", exerciseId)));
    }

    public List<OptionDto> getOptionsByExerciseId(UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", exerciseId));
        return answerMapper.toDtoList(exercise.getPredefinedAnswers());
    }

    @Transactional
    public ExerciseResponseDto updateExercise(UUID exerciseId, ExerciseRequestDto dto) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", exerciseId));

        LearningUnit parent = learningUnitRepository.findById(dto.parentUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent Unit (LU)", "id", dto.parentUnitId()));
        exercise.setParent(parent);

        exercise.setCorrectAnswerJson(dto.answer());
        exercise.setType(ExerciseType.valueOf(dto.type()));
        exercise.setQuestion(dto.question());
        exercise.setDifficulty(dto.difficulty());

        // Clear existing options
        exercise.getPredefinedAnswers().clear();

        // Add new options from DTO
        if (dto.options() != null && !dto.options().isEmpty()) {
            List<Answer> newAnswers = dto.options().stream()
                    .map(answerMapper::toEntity)
                    .peek(op -> op.setExercise(exercise))
                    .toList();
            exercise.getPredefinedAnswers().addAll(newAnswers);
        }

        return exerciseMapper.toDto(exerciseRepository.save(exercise));
    }

    @Transactional
    public ExerciseResponseDto saveExercise(ExerciseRequestDto dto) {
        Exercise exercise = exerciseMapper.toEntity(dto);

        LearningUnit parent = learningUnitRepository.findById(dto.parentUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent Unit (LU)", "id", dto.parentUnitId()));
        exercise.setParent(parent);

        if (dto.options() != null && !dto.options().isEmpty()) {
            List<Answer> answers = dto.options().stream()
                    .map(answerMapper::toEntity)
                    .peek(op -> op.setExercise(exercise))
                    .toList();
            exercise.setPredefinedAnswers(answers);
        }

        return exerciseMapper.toDto(exerciseRepository.save(exercise));
    }

    public void deleteExercise(UUID exerciseId) {
        exerciseRepository.deleteById(exerciseId);
    }

    public List<ExerciseResponseDto> getExercises(UUID lessonId, UUID typeId) {
        List<Exercise> exercises;

        if (lessonId != null) {
            exercises = exerciseRepository.findByParent_Id(lessonId);
        } else {
            exercises = exerciseRepository.findAll();
        }

        return exercises.stream()
                .map(exerciseMapper::toDto)
                .toList();
    }
}