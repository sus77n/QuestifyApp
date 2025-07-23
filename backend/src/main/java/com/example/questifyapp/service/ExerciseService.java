package com.example.questifyapp.service;

import com.example.questifyapp.dto.exercise.ExerciseRequestDto;
import com.example.questifyapp.dto.exercise.ExerciseResponseDto;
import com.example.questifyapp.dto.option.OptionResponseDto;
import com.example.questifyapp.dto.learningUnit.LearningUnitChildDto;
import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.entity.LearningUnit;
import com.example.questifyapp.entity.Option;
import com.example.questifyapp.mapper.ExerciseMapper;
import com.example.questifyapp.mapper.OptionMapper;
import com.example.questifyapp.repository.ExerciseRepository;
import com.example.questifyapp.repository.LearningUnitRepository;
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
