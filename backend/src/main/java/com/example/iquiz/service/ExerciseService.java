package com.example.iquiz.service;

import com.example.iquiz.dto.exercise.ExerciseRequestDto;
import com.example.iquiz.dto.exercise.ExerciseResponseDto;
import com.example.iquiz.dto.option.OptionResponseDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.ExerciseCategory;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.entity.Option;
import com.example.iquiz.mapper.ExerciseMapper;
import com.example.iquiz.mapper.OptionMapper;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.ExerciseTypeRepository;
import com.example.iquiz.repository.LearningUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Autowired
    private ExerciseTypeRepository exerciseTypeRepository;

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
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        LearningUnit parent = learningUnitRepository.findById(dto.parentUnitId())
                .orElseThrow(() -> new RuntimeException("Parent Unit not found"));
        exercise.setParent(parent);

        if (dto.exerciseTypeId() != null) {
            ExerciseCategory type = exerciseTypeRepository.findById(dto.exerciseTypeId())
                    .orElseThrow(() -> new RuntimeException("ExerciseType not found"));
            exercise.setExerciseCategory(type);
        }

        exercise.setAnswer(dto.answer());
        exercise.setType(dto.type());
        exercise.setQuestion(dto.question());
        exercise.setDifficulty(dto.difficulty());

        // --- clear options cũ ---
        exercise.getOptions().clear();

        // --- add lại options từ DTO ---
        if (dto.options() != null && !dto.options().isEmpty()) {
            List<Option> newOptions = dto.options().stream()
                    .map(optionMapper::toEntity)
                    .peek(op -> op.setExercise(exercise))
                    .toList();
            exercise.getOptions().addAll(newOptions);
        }

        return exerciseMapper.toDto(exerciseRepository.save(exercise));
    }


    public ExerciseResponseDto saveExercise(ExerciseRequestDto dto) {
        Exercise exercise = exerciseMapper.toEntity(dto);

        LearningUnit parent = learningUnitRepository.findById(dto.parentUnitId())
                .orElseThrow(() -> new RuntimeException("Parent Unit not found"));
        exercise.setParent(parent);

        if (dto.exerciseTypeId() != null) {
            ExerciseCategory type = exerciseTypeRepository.findById(dto.exerciseTypeId())
                    .orElseThrow(() -> new RuntimeException("ExerciseType not found"));
            exercise.setExerciseCategory(type);
        }

        if (dto.options() != null && !dto.options().isEmpty()) {
            List<Option> options = dto.options().stream()
                    .map(optionMapper::toEntity)
                    .peek(op -> op.setExercise(exercise))
                    .toList();
            exercise.setOptions(options);
        }

        return exerciseMapper.toDto(exerciseRepository.save(exercise));
    }

    public void deleteExercise(Long exerciseId) {
        exerciseRepository.deleteById(exerciseId);
    }

    public List<ExerciseResponseDto> getAllExercisesByUserId(Long userId) {
        return null;
    }

    public List<ExerciseResponseDto> getExercises(Long lessonId, Long typeId) {
        List<Exercise> exercises;

        if (lessonId != null && typeId != null) {
            exercises = exerciseRepository.findByParent_IdAndExerciseCategory_Id(lessonId, typeId);
        } else if (lessonId != null) {
            exercises = exerciseRepository.findByParent_Id(lessonId);
        } else if (typeId != null) {
            exercises = exerciseRepository.findByExerciseCategory_Id(typeId);
        } else {
            exercises = exerciseRepository.findAll();
        }

        return exercises.stream()
                .map(exerciseMapper::toDto)
                .toList();
    }

}
