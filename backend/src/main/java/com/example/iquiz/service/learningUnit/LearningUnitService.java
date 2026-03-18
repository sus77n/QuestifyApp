package com.example.iquiz.service.learningUnit;

import com.example.iquiz.dto.ai.GenerateExercisesRequest;
import com.example.iquiz.dto.exercise.ExerciseWithAnswerDto;
import com.example.iquiz.dto.learningUnit.*;
import com.example.iquiz.entity.*;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.mapper.ExerciseMapper;
import com.example.iquiz.mapper.LearningUnitMapper;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.LearningUnitRepository;
import com.example.iquiz.repository.ExerciseCategoryRepository;
import com.example.iquiz.repository.LessonConfigRepository;
import com.example.iquiz.service.AIService;
import com.example.iquiz.utility.LearningUnitUtil;
import com.example.iquiz.utility.UserUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class LearningUnitService {

    LearningUnitRepository learningUnitRepository;
    ExerciseCategoryRepository exerciseCategoryRepository;
    LearningUnitMapper learningUnitMapper;
    LearningUnitUtil learningUnitUtil;
    UserUtil userUtil;
    ExerciseMapper exerciseMapper;
    ExerciseRepository exerciseRepository;
    private final AIService aIService;
    private final LessonConfigRepository lessonConfigRepository;

    public List<LearningUnitDto> getAllLearningUnits() {
        return learningUnitRepository.findAll().stream()
                .map(learningUnitMapper::toDto)
                .toList();
    }

    public LearningUnitDto getLearningUnitById(UUID id, boolean includeCategory) {
        LearningUnit learningUnit = learningUnitRepository.findWithChildren(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", id));
        if (includeCategory) {
            return learningUnitMapper.toDto(learningUnit);
        } else {
            return learningUnitMapper.toDtoWithoutCategory(learningUnit);
        }
    }

    public LearningUnitWithStatisticDto getLearningUnitWithStatisticByIdAndStudentId(UUID id, UUID userId) {
        LearningUnit learningUnit = learningUnitRepository.findWithChildren(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", id));
        LearningUnitWithStatisticDto dto = learningUnitMapper.toDtoWithStatistic(learningUnit, userId);
        return dto;
    }

    public LearningUnitDto saveLearningUnit(LearningUnitDto dto) {
        User user = userUtil.getUserFromAuthContext();
        LearningUnit entity = learningUnitMapper.toEntity(dto);
        entity.setCreatedBy(user);
        entity = learningUnitRepository.save(entity);
        return learningUnitMapper.toDto(entity);
    }


    @Transactional
    public LessonDetailDto initializeLesson(UUID id) {
        LearningUnit lesson = learningUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", id));

        if (lesson.getLessonConfig() == null) {
            LessonConfig defaultLessonConfig = new LessonConfig();
            defaultLessonConfig.setLesson(lesson);
            defaultLessonConfig.setNoRepeatScope(true);
            defaultLessonConfig.setQuestionsPerAttempt(10);
            defaultLessonConfig.setPassThreshold(50);

            lesson.setLessonConfig(defaultLessonConfig);
        }

        if (lesson.getChildren() == null || lesson.getChildren().isEmpty()) {
            LearningUnitType type = exerciseCategoryRepository.findByName("Exercise Category")
                    .orElseThrow(() -> new ResourceNotFoundException("Learning Unit Type", "name", "Exercise Category"));
            LearningUnit exerciseCategory = new LearningUnit();
            exerciseCategory.setName("Default Exercise Category");
            exerciseCategory.setType(type);
            exerciseCategory.setParent(lesson);
            exerciseCategory.setCreatedBy(lesson.getCreatedBy());

            lesson.getChildren().add(exerciseCategory);
        }

        learningUnitRepository.save(lesson);

        return learningUnitMapper.toLessonDetailDto(lesson);
    }

    @Transactional
    public LessonDetailDto getLessonDetailsById(UUID id) {
        LearningUnit lesson = learningUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", id));

        return learningUnitMapper.toLessonDetailDto(lesson);
    }

    @Transactional
    public LearningUnitDto combineLearningUnit(CreateLearningUnitChildDto dto, List<UUID> selectedIds) {
        User user = userUtil.getUserFromAuthContext();
        LearningUnit parent = learningUnitRepository.findById(dto.parentId())
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", dto.parentId()));

        LearningUnitType type = parent.getType();
        int nextLevel = type.getLevel() + 1;
        LearningUnitType childType = exerciseCategoryRepository.findByLevel(nextLevel).stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit Type", "level", nextLevel));

        LearningUnit entity = new LearningUnit();
        entity.setName(dto.name());
        entity.setCreatedBy(user);
        entity.setType(childType);
        entity.setParent(parent);

        LearningUnit practice = learningUnitRepository.save(entity);

        List<LearningUnit> originals = learningUnitRepository.findLeafNodesFromSubtree(selectedIds);
        //Tổ chức code dở quáaaa
        List<CreateExerciseCategoryDto> copiedChildren = originals.stream()
                .map(original -> learningUnitMapper.toCreateExerciseCategoryDtoForGenerateExercise(original))
                .toList();


        aIService.generateExercises(new GenerateExercisesRequest(practice.getId(), copiedChildren));

        return learningUnitMapper.toDto(practice);
    }

    @Transactional
    public LearningUnitDto saveLearningUnitChild(CreateLearningUnitChildDto dto) {
        User user = userUtil.getUserFromAuthContext();
        LearningUnit parent = learningUnitRepository.findById(dto.parentId())
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", dto.parentId()));

        LearningUnitType type = parent.getType();
        int nextLevel = type.getLevel() + 1;
        LearningUnitType childType = exerciseCategoryRepository.findByLevel(nextLevel).stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit Type", "level", nextLevel));

        LearningUnit entity = new LearningUnit();
        entity.setName(dto.name());
        entity.setCreatedBy(user);
        entity.setType(childType);
        entity.setParent(parent);
        entity = learningUnitRepository.save(entity);
        return learningUnitMapper.toDto(entity);
    }

    @Transactional
    public List<LearningUnit> saveGeneratedCategoriesBulk(UUID parentId, List<CreateExerciseCategoryDto> dtos) {
        User user = userUtil.getUserFromAuthContext();

        LearningUnit lesson = learningUnitRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", parentId));

        List<LearningUnit> savedUnits = dtos.stream().map(dto -> {
            LearningUnitType type = exerciseCategoryRepository.findByName(dto.type())
                    .orElseThrow(() -> new ResourceNotFoundException("Learning Unit Type", "name", dto.type()));

            LearningUnit newCate = learningUnitMapper.generatedCategoryToEntity(dto);
            newCate.setParent(lesson);
            newCate.setType(type);
            newCate.setCreatedBy(user);

            if (newCate.getExercises() == null) {
                newCate.setExercises(new ArrayList<>());
            }

            LearningUnit savedCate = learningUnitRepository.save(newCate);

            List<Exercise> exercises = exerciseRepository.findAllById(dto.exerciseIds());
            exercises.forEach(ex -> ex.setParent(savedCate));

            exerciseRepository.saveAll(exercises);

            savedCate.getExercises().clear();
            savedCate.getExercises().addAll(exercises);

            return savedCate;
        }).toList();

        return savedUnits;
    }

    @Transactional
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
        LearningUnit unit = learningUnitRepository.findWithChildren(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", id));

        learningUnitRepository.delete(unit);
    }

    public List<LearningUnitWithStatisticDto> getLearningUnitsByTypeLevel(int level) {
        User user = userUtil.getUserFromAuthContext();
        return learningUnitRepository.findByTypeLevel(level).stream()
                .map(lu -> learningUnitMapper.toDtoWithStatistic(lu, user.getId()))
                .toList();
    }

    public long countByLearningUnitId(UUID id) {
        LearningUnit learningUnit = learningUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", id));
        return learningUnitRepository.countExercisesUnderLearningUnit(learningUnit.getId());
    }

    public List<LearningUnitWithStatisticDto> getIncompleteCourses() {
        User user = userUtil.getUserFromAuthContext();
        return learningUnitRepository.findIncompleteCoursesWithStatistics(user.getId());
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

