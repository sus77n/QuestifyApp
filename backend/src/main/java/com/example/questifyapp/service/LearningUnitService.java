package com.example.questifyapp.service;

import com.example.questifyapp.dto.learningUnit.LearningUnitDto;
import com.example.questifyapp.entity.LearningUnit;
import com.example.questifyapp.entity.LearningUnitType;
import com.example.questifyapp.mapper.LearningUnitMapper;
import com.example.questifyapp.mapper.LearningUnitTypeMapper;
import com.example.questifyapp.repository.LearningUnitRepository;
import com.example.questifyapp.repository.LearningUnitTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LearningUnitService {

    @Autowired
    private LearningUnitRepository learningUnitRepository;

    @Autowired
    private LearningUnitTypeRepository learningUnitTypeRepository;

    public List<LearningUnitDto> getAllLearningUnitTypes() {
        return learningUnitRepository.findAll().stream().
                map(LearningUnitMapper::toDto)
                .toList();
    }

    public LearningUnitDto getLearningUnitById(Long id) {
        return LearningUnitMapper.toDto(learningUnitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Learning Unit not found with id: " + id)));
    }

    public LearningUnitDto saveLearningUnit(LearningUnitDto learningUnitDto) {
        LearningUnit learningUnit = LearningUnitMapper.toEntity(learningUnitDto);

        LearningUnitType type = null;
        try {
            type = getLearningUnitType(LearningUnitTypeMapper.toEntity(learningUnitDto.type()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        learningUnit.setType(type);

        LearningUnit parent = learningUnitRepository.findById(learningUnitDto.parentId()).orElse(null);
        learningUnit.setParent(parent);

        learningUnit = learningUnitRepository.save(learningUnit);
        return LearningUnitMapper.toDto(learningUnit);
    }

    public LearningUnitDto updateLearningUnit(Long id, LearningUnitDto learningUnitDto) {
        LearningUnit learningUnit = learningUnitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Learning Unit not found with id: " + id));

        LearningUnitType type = null;
        try {
            type = getLearningUnitType(LearningUnitTypeMapper.toEntity(learningUnitDto.type()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        learningUnit.setType(type);

        LearningUnit parent = learningUnitRepository.findById(learningUnitDto.parentId()).orElse(null);
        learningUnit.setParent(parent);

        learningUnit.setName(learningUnitDto.name());
        learningUnit.setCode(learningUnitDto.code());
        learningUnit.setDescription(learningUnitDto.description());
        learningUnit.setStatus(learningUnitDto.status());
        learningUnit = learningUnitRepository.save(learningUnit);
        return LearningUnitMapper.toDto(learningUnit);
    }


    public List<LearningUnitDto> getLearningUnitsByTypeLevel(int level) {
        List<LearningUnit> learningUnits = learningUnitRepository.findByTypeLevel(level);
        return learningUnits.stream().map(LearningUnitMapper::toDto).collect(Collectors.toList());
    }


    private LearningUnitType getLearningUnitType(LearningUnitType type) throws Exception {

        if (type == null) {
            return null;
        }

        if (type.getId() != null) {
            type = learningUnitTypeRepository.findById(type.getId()).orElse(null);
        } else if (type.getName() != null) {
            type = learningUnitTypeRepository.findByName(type.getName());
        } else if (type.getLevel() != 0) {
            type = learningUnitTypeRepository.findByLevel(type.getLevel());
        } else {
            try {
                throw new Exception("Learning Type not found");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return type;
    }
}
