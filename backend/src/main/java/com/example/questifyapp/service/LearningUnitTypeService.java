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
    
    @Autowired
    private LearningUnitTypeMapper learningUnitTypeMapper;

    public LearningUnitTypeDto getLearningUnitTypeById(Long id) {
        return learningUnitTypeMapper.toDto(learningUnitTypeRepository.findById(id).get());
    }

    public List<LearningUnitTypeDto> getLearningUnitTypes() {
        return learningUnitTypeRepository.findAll().stream().map(learningUnitType -> learningUnitTypeMapper.toDto(learningUnitType)).collect(Collectors.toList());
    }

    public LearningUnitTypeDto getLearningUnitTypeByType(String type) {
        return learningUnitTypeMapper.toDto(learningUnitTypeRepository.findByName(type));
    }

    public LearningUnitTypeDto saveLearningUnitType(LearningUnitTypeDto learningUnitTypeDto) {
        LearningUnitType learningUnitType = learningUnitTypeMapper.toEntity(learningUnitTypeDto);
        learningUnitTypeRepository.save(learningUnitType);
        return learningUnitTypeMapper.toDto(learningUnitType);
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
        return learningUnitTypeMapper.toDto(learningUnitType);
    }
}
