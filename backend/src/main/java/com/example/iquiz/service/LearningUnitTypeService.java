package com.example.iquiz.service;

import com.example.iquiz.dto.LearningUnitTypeDto;
import com.example.iquiz.entity.LearningUnitType;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.mapper.LearningUnitTypeMapper;
import com.example.iquiz.repository.ExerciseCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LearningUnitTypeService {

    private final ExerciseCategoryRepository exerciseCategoryRepository;
    private final LearningUnitTypeMapper learningUnitTypeMapper;

    public LearningUnitTypeDto getLearningUnitTypeById(UUID id) {
        LearningUnitType type = exerciseCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Learning Unit Type not found with id: " + id));
        return learningUnitTypeMapper.toDto(type);
    }

    public List<LearningUnitTypeDto> getLearningUnitTypes() {
        return exerciseCategoryRepository.findAll().stream()
                .map(learningUnitTypeMapper::toDto)
                .toList();
    }

    public LearningUnitTypeDto saveLearningUnitType(LearningUnitTypeDto dto) {
        LearningUnitType entity = learningUnitTypeMapper.toEntity(dto);
        exerciseCategoryRepository.save(entity);
        return learningUnitTypeMapper.toDto(entity);
    }

    public void deleteLearningUnitTypeById(UUID id) {
        if (!exerciseCategoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Learning Unit Type not found with id: " + id);
        }
        exerciseCategoryRepository.deleteById(id);
    }

    public LearningUnitTypeDto updateLearningUnitType(UUID id, LearningUnitTypeDto dto) {
        LearningUnitType entity = exerciseCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Learning Unit Type not found with id: " + id));

        entity.setName(dto.type());
        entity.setLevel(dto.level());

        // Nếu rule: nếu level = 0 thì shift tất cả level khác lên +1
        if (dto.level() == 0) {
            List<LearningUnitType> all = exerciseCategoryRepository.findAll();
            all.forEach(l -> l.setLevel(l.getLevel() + 1));
            exerciseCategoryRepository.saveAll(all);
        }

        exerciseCategoryRepository.save(entity);
        return learningUnitTypeMapper.toDto(entity);
    }
}

