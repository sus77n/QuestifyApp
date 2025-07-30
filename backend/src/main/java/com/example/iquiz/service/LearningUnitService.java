package com.example.iquiz.service;

import com.example.iquiz.dto.learningUnit.CourseDto;
import com.example.iquiz.dto.learningUnit.LearningUnitDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.entity.LearningUnitType;
import com.example.iquiz.mapper.LearningUnitMapper;
import com.example.iquiz.mapper.LearningUnitTypeMapper;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.LearningUnitRepository;
import com.example.iquiz.repository.LearningUnitTypeRepository;
import com.example.iquiz.repository.SubmissionRepository;
import com.example.iquiz.utility.LearningUnitUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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

    @Autowired
    private LearningUnitMapper learningUnitMapper;

    @Autowired
    private ExerciseRepository exerciseRepository;

    public List<LearningUnitDto> getAllLearningUnitTypes() {
        return learningUnitRepository.findAll().stream().
                map(learningUnit -> learningUnitMapper.toDto(learningUnit))
                .toList();
    }

    public LearningUnitDto getLearningUnitById(Long id, Long userId) {
        LearningUnit learningUnit = learningUnitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Learning Unit not found with id: " + id));

        List<Exercise> allUnsubmitted = exerciseRepository
                .findUnsubmittedExercisesByUserIdAndUnitId(learningUnit.getId(), userId);
        if (allUnsubmitted.size() >= 5) {
            Collections.shuffle(allUnsubmitted);

            List<Exercise> selected = allUnsubmitted.stream()
                    .limit(5)
                    .toList();
            learningUnit.setExercises(selected);
        }
        return learningUnitMapper.toDto(learningUnit);
    }

    public LearningUnitDto saveLearningUnit(LearningUnitDto learningUnitDto) {
        LearningUnit learningUnit = learningUnitMapper.toEntity(learningUnitDto);

        learningUnit = learningUnitRepository.save(learningUnit);
        return learningUnitMapper.toDto(learningUnit);
    }

    public LearningUnitDto updateLearningUnit(Long id, LearningUnitDto dto) {
        LearningUnit learningUnit = learningUnitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Learning Unit not found with id: " + id));

        LearningUnitType type = learningUnitTypeRepository.findByName(dto.type());
        learningUnit.setType(type);

        LearningUnit parent = learningUnitRepository.findById(dto.parentId()).orElse(null);
        learningUnit.setParent(parent);

        learningUnit.setName(dto.name());
        learningUnit.setCode(dto.code());
        learningUnit.setDescription(dto.description());
        learningUnit.setStatus(dto.status());
        learningUnit = learningUnitRepository.save(learningUnit);
        return learningUnitMapper.toDto(learningUnit);
    }


    public List<LearningUnitDto> getLearningUnitsByTypeLevel(int level) {
        List<LearningUnit> learningUnits = learningUnitRepository.findByTypeLevel(level);
        return learningUnits.stream().map(learningUnit -> learningUnitMapper.toDto(learningUnit)).collect(Collectors.toList());
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
