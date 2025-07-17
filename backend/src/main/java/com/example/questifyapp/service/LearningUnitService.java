package com.example.questifyapp.service;

import com.example.questifyapp.dto.learningUnit.CourseDto;
import com.example.questifyapp.dto.learningUnit.LearningUnitDto;
import com.example.questifyapp.entity.LearningUnit;
import com.example.questifyapp.entity.LearningUnitType;
import com.example.questifyapp.mapper.LearningUnitMapper;
import com.example.questifyapp.mapper.LearningUnitTypeMapper;
import com.example.questifyapp.repository.LearningUnitRepository;
import com.example.questifyapp.repository.LearningUnitTypeRepository;
import com.example.questifyapp.repository.SubmissionRepository;
import com.example.questifyapp.utility.LearningUnitUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LearningUnitService {

    @Autowired
    private LearningUnitRepository learningUnitRepository;

    @Autowired
    private LearningUnitTypeRepository learningUnitTypeRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    public List<LearningUnitDto> getAllLearningUnitTypes() {
        return learningUnitRepository.findAll().stream().
                map(LearningUnitMapper::toDto)
                .toList();
    }

    public LearningUnitDto getLearningUnitById(Long id) {
        LearningUnit learningUnit = learningUnitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Learning Unit not found with id: " + id));

        return LearningUnitMapper.toDto(learningUnit);
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

    public long countByLearningUnitId(Long id) {
        LearningUnit learningUnit = learningUnitRepository.findById(id).orElseThrow(() -> new NullPointerException("Learning Unit not found with id: " + id));
        return LearningUnitUtil.countExercises(learningUnit);
    }

    public List<CourseDto> getAllCoursesWithUserId(Long userId) {
        List<CourseDto> courseList = new ArrayList<>();

        List<LearningUnit> courses = learningUnitRepository.findByTypeLevel(1);

        for (LearningUnit learningUnit : courses) {
            Long totalExercise = countByLearningUnitId(learningUnit.getId());
            Long completedExercises = getNumberOfCompletedExercise(userId, learningUnit);
            courseList.add(new CourseDto(
                    learningUnit.getId(),
                    learningUnit.getName(),
                    learningUnit.getCode(),
                    totalExercise,
                    completedExercises
            ));
        }

        return courseList;
    }


    private Long getNumberOfCompletedExercise(Long userId, LearningUnit learningUnit) {
        if (learningUnit.getExercises() != null && learningUnit.getExercises().size() > 0) {
            return submissionRepository.countPassedExercisesByUserIdAndLearningUnitId(userId, learningUnit.getId());
        }

        Long result = 0L;
        for (LearningUnit lu : learningUnit.getChildren()) {
            Long count = getNumberOfCompletedExercise(userId, lu);
            if (count != null) {
                result += count;
            }
        }

        return result > 0 ? result : null;
    }
}
