package com.example.iquiz.service.learningUnit;

import com.example.iquiz.dto.exercise.ExerciseWithAnswerDto;
import com.example.iquiz.dto.learningUnit.CreateLearningUnitChildDto;
import com.example.iquiz.dto.learningUnit.LearningUnitChildDto;
import com.example.iquiz.dto.learningUnit.LearningUnitDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.entity.LearningUnitType;
import com.example.iquiz.entity.User;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.mapper.ExerciseMapper;
import com.example.iquiz.mapper.LearningUnitMapper;
import com.example.iquiz.repository.LearningUnitRepository;
import com.example.iquiz.repository.ExerciseCategoryRepository;
import com.example.iquiz.utility.LearningUnitUtil;
import com.example.iquiz.utility.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LearningUnitService {

    private final LearningUnitRepository learningUnitRepository;
    private final ExerciseCategoryRepository exerciseCategoryRepository;
    private final LearningUnitMapper learningUnitMapper;
    private final LearningUnitUtil learningUnitUtil;
    private final UserUtil userUtil;
    private final ExerciseMapper exerciseMapper;

    public List<LearningUnitDto> getAllLearningUnits() {
        return learningUnitRepository.findAll().stream()
                .map(learningUnitMapper::toDto)
                .toList();
    }

    public LearningUnitDto getLearningUnitById(UUID id) {
        LearningUnit learningUnit = learningUnitRepository.findWithChildren(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", id));

        return learningUnitMapper.toDto(learningUnit);
    }

    public LearningUnitDto getLearningUnitByIdWithAuth(UUID id, UUID userId) {
        LearningUnit learningUnit = learningUnitRepository.findWithChildren(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", id));

        return learningUnitMapper.toDtoWithAuth(learningUnit, userId);
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
        LearningUnitType childType = exerciseCategoryRepository.findByLevel(nextLevel)
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
            LearningUnitType type = exerciseCategoryRepository.findByName(dto.type())
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
                .map(learningUnitMapper::toDtoShallow)
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

    @Transactional
    public List<ExerciseWithAnswerDto> getExerciseIdsByLearningUnitId(UUID id) {
        LearningUnit unit = learningUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", id));

        List<Exercise> exercises = learningUnitUtil.getAllExercises(unit);

        return exercises.stream()
                .map(exerciseMapper::toDtoWithAnswer)
                .toList();
    }

}

