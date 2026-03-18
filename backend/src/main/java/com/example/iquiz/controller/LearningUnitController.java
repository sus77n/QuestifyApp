package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.dto.exercise.ExerciseWithAnswerDto;
import com.example.iquiz.dto.learningUnit.*;
import com.example.iquiz.service.learningUnit.LearningUnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/learning-units")
@RequiredArgsConstructor
public class LearningUnitController {

    @Autowired
    private final LearningUnitService learningUnitService;

    @GetMapping
    public ApiResponse<List<LearningUnitDto>> getAll() {
        List<LearningUnitDto> list = learningUnitService.getAllLearningUnits();
        return ApiResponse.success(list, "Fetched all learning units");
    }

    @PostMapping
    public ApiResponse<LearningUnitDto> createLearningUnit(@Valid @RequestBody LearningUnitDto dto) {
        LearningUnitDto created = learningUnitService.saveLearningUnit(dto);
        return ApiResponse.success(created, "Learning unit created successfully");
    }

    @PostMapping("/combined")
    public ApiResponse<LearningUnitDto> createCombinedLearningUnit(@Valid @RequestBody CreateLearningUnitChildDto dto, List<UUID> selectedIds) {
        LearningUnitDto created = learningUnitService.combineLearningUnit(dto, selectedIds);
        return ApiResponse.success(created, "Learning unit created successfully");
    }

    @PostMapping("/{id}")
    public ApiResponse<LearningUnitDtoInterface> initializeLesson(@PathVariable UUID id) {
        LessonDetailDto lesson = learningUnitService.initializeLesson(id);
        return ApiResponse.success(lesson, "Lesson initialized successfully");
    }

    @PostMapping("/child")
    public ApiResponse<LearningUnitDto> createLearningUnitChild(@Valid @RequestBody CreateLearningUnitChildDto dto) {
        LearningUnitDto created = learningUnitService.saveLearningUnitChild(dto);
        return ApiResponse.success(created, "Learning unit created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<LearningUnitDtoInterface> getLearningUnitById(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false, defaultValue = "false") boolean includeCategory
    ) {
        LearningUnitDtoInterface dto;
        if (userId != null) {
            dto = learningUnitService.getLearningUnitWithStatisticByIdAndStudentId(id, userId);
        } else {
            dto = learningUnitService.getLearningUnitById(id, includeCategory);
        }
        return ApiResponse.success(dto, "Fetched learning unit successfully");
    }

    @GetMapping("/{id}/lesson-details")
    public ApiResponse<LearningUnitDtoInterface> getLearningUnitById(
            @PathVariable UUID id
    ) {
        LearningUnitDtoInterface dto = learningUnitService.getLessonDetailsById(id);
        return ApiResponse.success(dto, "Fetched Lesson Details successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<LearningUnitDto> updateLearningUnit(
            @PathVariable UUID id,
            @Valid @RequestBody LearningUnitDto dto
    ) {
        LearningUnitDto updated = learningUnitService.updateLearningUnit(id, dto);
        return ApiResponse.success(updated, "Learning unit updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteLearningUnit(@PathVariable UUID id) {
        learningUnitService.deleteLearningUnit(id);
        return ApiResponse.success(null, "Learning unit deleted successfully");
    }

    @GetMapping("/type/level/{level}")
    public ApiResponse<List<LearningUnitWithStatisticDto>> getLearningUnitsByTypeLevel(@PathVariable int level) {
        List<LearningUnitWithStatisticDto> list = learningUnitService.getLearningUnitsByTypeLevel(level);
        return ApiResponse.success(list, "Fetched learning units by type level");
    }

    @GetMapping("/count/{id}")
    public ApiResponse<Long> countLearningUnitById(@PathVariable UUID id) {
        Long count = learningUnitService.countByLearningUnitId(id);
        return ApiResponse.success(count, "Counted learning units successfully");
    }

    @GetMapping("/courses/completed/{userId}")
    public ApiResponse<List<LearningUnitWithStatisticDto>> getCompletedCourses(@PathVariable UUID userId) {
        List<LearningUnitWithStatisticDto> completed = learningUnitService.getIncompleteCourses().stream()
                .filter(c -> c.getNumberOfComplete() > 0 && c.getNumberOfComplete() >= c.getNumberOfExercise())
                .toList();
        return ApiResponse.success(completed, "Fetched completed courses");
    }

    @GetMapping("/courses/incompleted/{userId}")
    public ApiResponse<List<LearningUnitWithStatisticDto>> getIncompletedCourses(@PathVariable UUID userId) {
        return ApiResponse.success(
                learningUnitService.getIncompleteCourses(),
                "Fetched incomplete courses"
        );
    }

    @GetMapping("/{id}/exercises")
    public ApiResponse<List<ExerciseWithAnswerDto>> getExerciseIdsByCourseId(@PathVariable UUID id) {
        List<ExerciseWithAnswerDto> exerciseDtos = learningUnitService.getExerciseIdsByLearningUnitId(id);
        return ApiResponse.success(exerciseDtos, "Fetched exercises for the course");
    }

}
