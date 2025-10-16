package com.example.iquiz.service;

import com.example.iquiz.dto.LearningUnitTypeDto;
import com.example.iquiz.entity.LearningUnitType;
import com.example.iquiz.mapper.LearningUnitTypeMapper;
import com.example.iquiz.repository.LearningUnitTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningUnitTypeService {

    private final LearningUnitTypeRepository learningUnitTypeRepository;
    private final LearningUnitTypeMapper learningUnitTypeMapper;

    public LearningUnitTypeDto getLearningUnitTypeById(Long id) {
        LearningUnitType type = learningUnitTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Learning Unit Type not found with id: " + id));
        return learningUnitTypeMapper.toDto(type);
    }

    public List<LearningUnitTypeDto> getLearningUnitTypes() {
        return learningUnitTypeRepository.findAll().stream()
                .map(learningUnitTypeMapper::toDto)
                .toList();
    }

    public LearningUnitTypeDto getLearningUnitTypeByType(String type) {
        return learningUnitTypeMapper.toDto(
                learningUnitTypeRepository.findByName(type)
        );
    }

    public LearningUnitTypeDto saveLearningUnitType(LearningUnitTypeDto dto) {
        LearningUnitType entity = learningUnitTypeMapper.toEntity(dto);
        learningUnitTypeRepository.save(entity);
        return learningUnitTypeMapper.toDto(entity);
    }

    public void deleteLearningUnitTypeById(Long id) {
        if (!learningUnitTypeRepository.existsById(id)) {
            throw new IllegalArgumentException("Learning Unit Type not found with id: " + id);
        }
        learningUnitTypeRepository.deleteById(id);
    }

    public LearningUnitTypeDto updateLearningUnitType(Long id, LearningUnitTypeDto dto) {
        LearningUnitType entity = learningUnitTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Learning Unit Type not found with id: " + id));

        entity.setName(dto.type());
        entity.setLevel(dto.level());

        // Nếu rule: nếu level = 0 thì shift tất cả level khác lên +1
        if (dto.level() == 0) {
            List<LearningUnitType> all = learningUnitTypeRepository.findAll();
            all.forEach(l -> l.setLevel(l.getLevel() + 1));
            learningUnitTypeRepository.saveAll(all);
        }

        learningUnitTypeRepository.save(entity);
        return learningUnitTypeMapper.toDto(entity);
    }
}

