package com.example.iquiz.service;

import com.example.iquiz.dto.ExerciseTypeDto;
import com.example.iquiz.entity.ExerciseType;
import com.example.iquiz.mapper.ExerciseTypeMapper;
import com.example.iquiz.repository.ExerciseTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseTypeService {
    @Autowired
    private ExerciseTypeRepository exerciseTypeRepository;
    @Autowired
    private ExerciseTypeMapper exerciseTypeMapper;

    public List<ExerciseTypeDto> getAllExerciseTypes() {
        return exerciseTypeRepository.findAll().stream()
                .map(exerciseType -> exerciseTypeMapper.toDto(exerciseType)).collect(Collectors.toList());
    }

    public ExerciseTypeDto getExerciseTypeById(Long id) {
        return exerciseTypeMapper.toDto(exerciseTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No exercise type id: " + id)));
    }

    public ExerciseTypeDto saveExerciseType(ExerciseTypeDto exerciseTypeDto) {
        ExerciseType exerciseType = exerciseTypeMapper.toEntity(exerciseTypeDto);
        return exerciseTypeMapper.toDto(exerciseTypeRepository.save(exerciseType));
    }

    public ExerciseTypeDto updateExerciseType(Long id, ExerciseTypeDto dto) {
        ExerciseType exerciseType = exerciseTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No exercise type id: " + id));

        exerciseType.setCode(dto.code());
        return exerciseTypeMapper.toDto(exerciseTypeRepository.save(exerciseType));
    }

    public void deleteExerciseTypeById(Long id) {
        exerciseTypeRepository.deleteById(id);
    }
}
