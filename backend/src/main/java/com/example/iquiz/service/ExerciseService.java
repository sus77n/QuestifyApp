package com.example.iquiz.service;

import com.example.iquiz.dto.exercise.ExerciseRequestDto;
import com.example.iquiz.dto.exercise.ExerciseResponseDto;
import com.example.iquiz.dto.option.OptionResponseDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.mapper.ExerciseMapper;
import com.example.iquiz.mapper.OptionMapper;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.LearningUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
    private OptionMapper optionMapper;

    public List<ExerciseResponseDto> getAllExercises() {
        return exerciseRepository.findAll()
                .stream().map(exercise ->  exerciseMapper.toDto(exercise)).collect(Collectors.toList());
    }

    public ExerciseResponseDto getExerciseById(Long exerciseId) {
        return exerciseMapper.toDto(exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NullPointerException("Exercise with id: " + exerciseId + " not found!")));
    }

    public List<OptionResponseDto> getOptionsByExerciseId(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NullPointerException("Exercise with id: " + exerciseId + " does not exist"));
        return optionMapper.toDtoList(exercise.getOptions());
    }

    public ExerciseResponseDto updateExercise(Long exerciseId, ExerciseRequestDto dto) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NullPointerException("Exercise with id: " + exerciseId + " does not exist"));

        LearningUnit parent = learningUnitRepository.findById(dto.parentUnitId())
                .orElseThrow(() -> new NullPointerException("Parent Unit with id: " + dto.parentUnitId() + " does not exist"));
        exercise.setParent(parent);

        exercise.setUpdatedAt(LocalDateTime.now());
        exercise.setAnswer(dto.answer());
        exercise.setType(dto.type());
        exercise.setQuestion(dto.question());

        exerciseRepository.save(exercise);
        return exerciseMapper.toDto(exercise);
    }

    public ExerciseResponseDto saveExercise(ExerciseRequestDto dto) {
        Exercise exercise = exerciseMapper.toEntity(dto);
        exercise.setUpdatedAt(LocalDateTime.now());
        exercise.setCreatedAt(LocalDateTime.now());

        exerciseRepository.save(exercise);
        return exerciseMapper.toDto(exercise);
    }

    public void deleteExercise(Long exerciseId) {
        exerciseRepository.deleteById(exerciseId);
    }
}
