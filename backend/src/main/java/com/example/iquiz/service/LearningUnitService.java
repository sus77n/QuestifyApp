package com.example.iquiz.service;

import com.example.iquiz.dto.learningUnit.CourseDto;
import com.example.iquiz.dto.learningUnit.LearningUnitDto;
import com.example.iquiz.dto.learningUnit.LearningUnitTreeDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.entity.LearningUnitType;
import com.example.iquiz.mapper.LearningUnitMapper;
import com.example.iquiz.mapper.LearningUnitTreeMapper;
import com.example.iquiz.mapper.LearningUnitTypeMapper;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.LearningUnitRepository;
import com.example.iquiz.repository.LearningUnitTypeRepository;
import com.example.iquiz.repository.SubmissionRepository;
import com.example.iquiz.utility.LearningUnitUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningUnitService {

    private final LearningUnitRepository learningUnitRepository;
    private final LearningUnitTypeRepository learningUnitTypeRepository;
    private final LearningUnitMapper learningUnitMapper;
    private final LearningUnitUtil learningUnitUtil;
    private final LearningUnitTreeMapper treeMapper;


    public List<LearningUnitDto> getAllLearningUnits() {
        return learningUnitRepository.findAll().stream()
                .map(learningUnitMapper::toDto)
                .toList();
    }

    public LearningUnitDto getLearningUnitById(Long id, Long userId) {
        LearningUnit learningUnit = learningUnitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Learning Unit not found with id: " + id));

        if (learningUnit.getType().getName().equalsIgnoreCase("lesson")) {
            return learningUnitMapper.toLessonDto(learningUnit, userId);
        }

        return learningUnitMapper.toDto(learningUnit, userId);
    }


    public LearningUnitTreeDto getLearningUnitWithChildren(Long courseId) {
        LearningUnit course = learningUnitRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id " + courseId));
        return treeMapper.toTreeDto(course);
    }


    public LearningUnitDto saveLearningUnit(LearningUnitDto dto) {
        LearningUnit entity = learningUnitMapper.toEntity(dto);
        entity = learningUnitRepository.save(entity);
        return learningUnitMapper.toDto(entity);
    }

    public LearningUnitDto updateLearningUnit(Long id, LearningUnitDto dto) {
        LearningUnit learningUnit = learningUnitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Learning Unit not found with id: " + id));

        if (dto.id() != null) {
            LearningUnitType type = learningUnitTypeRepository.findById(dto.id())
                    .orElseThrow(() -> new EntityNotFoundException("Type not found"));
            learningUnit.setType(type);
        }

        if (dto.parentId() != null) {
            LearningUnit parent = learningUnitRepository.findById(dto.parentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent not found"));
            learningUnit.setParent(parent);
        }

        learningUnit.setName(dto.name());
        learningUnit.setCode(dto.code());
        learningUnit.setDescription(dto.description());
        learningUnit.setStatus(dto.status());

        learningUnit = learningUnitRepository.save(learningUnit);
        return learningUnitMapper.toDto(learningUnit);
    }

    public void deleteLearningUnit(Long id) {
        if (!learningUnitRepository.existsById(id)) {
            throw new EntityNotFoundException("Learning Unit not found with id: " + id);
        }
        learningUnitRepository.deleteById(id);
    }

    public List<LearningUnitDto> getLearningUnitsByTypeLevel(int level) {
        return learningUnitRepository.findByTypeLevel(level).stream()
                .map(learningUnitMapper::toDto)
                .toList();
    }

    public long countByLearningUnitId(Long id) {
        LearningUnit learningUnit = learningUnitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Learning Unit not found with id: " + id));
        return learningUnitUtil.countExercises(learningUnit);
    }

    public List<CourseDto> getAllCoursesWithUserId(Long userId) {
        List<LearningUnit> courseUnit = learningUnitRepository.findAllByType_Name("Course");
        return courseUnit.stream()
                .map(course -> new CourseDto(
                        course.getId(),
                        course.getName(),
                        course.getCode(),
                        learningUnitUtil.countExercises(course),
                        learningUnitUtil.getNumberOfCompletedExercise(userId, course)
                ))
                .toList();
    }
}

