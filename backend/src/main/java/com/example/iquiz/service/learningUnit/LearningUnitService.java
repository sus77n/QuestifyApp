package com.example.iquiz.service.learningUnit;

import com.example.iquiz.dto.learningUnit.CreateLearningUnitChildDto;
import com.example.iquiz.dto.learningUnit.LearningUnitChildDto;
import com.example.iquiz.dto.learningUnit.LearningUnitDto;
import com.example.iquiz.dto.learningUnit.LearningUnitTreeDto;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.entity.LearningUnitType;
import com.example.iquiz.entity.User;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.mapper.LearningUnitMapper;
import com.example.iquiz.repository.LearningUnitRepository;
import com.example.iquiz.repository.LearningUnitTypeRepository;
import com.example.iquiz.utility.LearningUnitUtil;
import com.example.iquiz.utility.UserUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LearningUnitService {

    private final LearningUnitRepository learningUnitRepository;
    private final LearningUnitTypeRepository learningUnitTypeRepository;
    private final LearningUnitMapper learningUnitMapper;
    private final LearningUnitUtil learningUnitUtil;
    private final UserUtil userUtil;

    public List<LearningUnitDto> getAllLearningUnits() {
        return learningUnitRepository.findAll().stream()
                .map(learningUnitMapper::toDto)
                .toList();
    }

    public LearningUnitDto getLearningUnitById(UUID id, UUID userId) {
        LearningUnit learningUnit = learningUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", id));

        return learningUnitMapper.toDtoWithAuth(learningUnit, userId);
    }


    public LearningUnitTreeDto getLearningUnitWithChildren(UUID courseId) {
        LearningUnit course = learningUnitRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id " + courseId));
        return learningUnitMapper.toTreeDto(course);
    }


    public LearningUnitDto saveLearningUnit(LearningUnitDto dto) {
        User user = userUtil.getUserFromAuthContext();
        LearningUnit entity = learningUnitMapper.toEntity(dto);
        entity.setCreatedBy(user);
        entity = learningUnitRepository.save(entity);
        return learningUnitMapper.toDto(entity);
    }

    public LearningUnitDto saveLearningUnitChild(CreateLearningUnitChildDto dto) {
        User user = userUtil.getUserFromAuthContext();
        LearningUnit parent = learningUnitRepository.findById(dto.parentId())
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", dto.parentId()));

        LearningUnitType type = parent.getType();
        int nextLevel = type.getLevel() + 1;
        LearningUnitType childType = learningUnitTypeRepository.findByLevel(nextLevel)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit Type", "level", nextLevel));

        LearningUnit entity = new LearningUnit();
        entity.setName(dto.name());
        entity.setCreatedBy(user);
        entity.setType(childType);
        entity.setParent(parent);
        entity = learningUnitRepository.save(entity);
        return learningUnitMapper.toDto(entity);
    }

    public LearningUnitDto updateLearningUnit(UUID id, LearningUnitDto dto) {
        LearningUnit learningUnit = learningUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", id));

        if (dto.type() != null) {
            LearningUnitType type = learningUnitTypeRepository.findByName(dto.type())
                    .orElseThrow(() -> new ResourceNotFoundException("Learning Unit Type", "name", dto.type()));
            learningUnit.setType(type);
        }

        if (dto.parentId() != null) {
            LearningUnit parent = learningUnitRepository.findById(dto.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", dto.parentId()));
            learningUnit.setParent(parent);
        }

        learningUnit.setName(dto.name());
        learningUnit.setCode(dto.code());
        learningUnit.setDescription(dto.description());
        learningUnit.setStatus(dto.status());

        learningUnit = learningUnitRepository.save(learningUnit);
        return learningUnitMapper.toDto(learningUnit);
    }

    public void deleteLearningUnit(UUID id) {
        if (!learningUnitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Learning Unit", "id", id);
        }
        learningUnitRepository.deleteById(id);
    }

    public List<LearningUnitDto> getLearningUnitsByTypeLevel(int level) {
        return learningUnitRepository.findByTypeLevel(level).stream()
                .map(learningUnitMapper::toDto)
                .toList();
    }

    public long countByLearningUnitId(UUID id) {
        LearningUnit learningUnit = learningUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", id));
        return learningUnitUtil.countExercises(learningUnit);
    }

    public List<LearningUnitChildDto> getAllExerciseStatisticLUWithUserId(UUID userId) {
        List<LearningUnit> courseUnit = learningUnitRepository.findAllByType_Name("Course");
        return courseUnit.stream()
                .map(course -> new LearningUnitChildDto(
                        course.getId(),
                        course.getName(),
                        course.getCode(),
                        course.getType() != null ? course.getType().getName() : null,
                        learningUnitUtil.getNumberOfCompletedExercise(userId, course),
                        learningUnitUtil.countExercises(course)
                ))
                .toList();
    }

}

