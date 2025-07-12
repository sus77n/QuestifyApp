package com.example.questifyapp.service;

import com.example.questifyapp.dto.LearningUnitTypeDto;
import com.example.questifyapp.entity.LearningUnitType;
import com.example.questifyapp.mapper.LearningUnitTypeMapper;
import com.example.questifyapp.repository.LearningUnitTypeRepository;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LearningUnitTypeService {

    @Autowired
    private LearningUnitTypeRepository learningUnitTypeRepository;

    public LearningUnitTypeDto getLearningUnitTypeById(Long id) {
        return LearningUnitTypeMapper.toDto(learningUnitTypeRepository.findById(id).get());
    }

    public List<LearningUnitTypeDto> getLearningUnitTypes() {
        return learningUnitTypeRepository.findAll().stream().map(LearningUnitTypeMapper::toDto).collect(Collectors.toList());
    }

    public LearningUnitTypeDto getLearningUnitTypeByType(String type) {
        return LearningUnitTypeMapper.toDto(learningUnitTypeRepository.findByName(type));
    }

    public LearningUnitTypeDto saveLearningUnitType(LearningUnitTypeDto learningUnitTypeDto) {
        LearningUnitType learningUnitType = LearningUnitTypeMapper.toEntity(learningUnitTypeDto);
        learningUnitTypeRepository.save(learningUnitType);
        return LearningUnitTypeMapper.toDto(learningUnitType);
    }

    public void deleteLearningUnitTypeById(Long id) {
        learningUnitTypeRepository.deleteById(id);
    }

    public LearningUnitTypeDto updateLearningUnitType(Long id, LearningUnitTypeDto learningUnitTypeDto) {
        LearningUnitType learningUnitType = learningUnitTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Can not found unit type id:" + id));

        learningUnitType.setName(learningUnitType.getName());
        learningUnitType.setLevel(learningUnitType.getLevel());

        if (learningUnitType.getLevel() == 0) {
            learningUnitTypeRepository.findAll().forEach(l -> l.setLevel(l.getLevel() + 1));
        }

        learningUnitTypeRepository.save(learningUnitType);
        return LearningUnitTypeMapper.toDto(learningUnitType);
    }
}
