package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.dto.exercise.ExerciseWithAnswerDto;
import com.example.iquiz.dto.learningUnit.CreateExerciseCategoryDto;
import com.example.iquiz.dto.learningUnit.CreateLearningUnitChildDto;
import com.example.iquiz.dto.learningUnit.LearningUnitChildDto;
import com.example.iquiz.dto.learningUnit.LearningUnitDto;
import com.example.iquiz.service.AIService;
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

    @Autowired
    private final AIService aIService;

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

    @PostMapping("/child")
    public ApiResponse<LearningUnitDto> createLearningUnitChild(@Valid @RequestBody CreateLearningUnitChildDto dto) {
        LearningUnitDto created = learningUnitService.saveLearningUnitChild(dto);
        return ApiResponse.success(created, "Learning unit created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<LearningUnitDto> getLearningUnitById(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID userId
    ) {
        LearningUnitDto dto;
        if (userId != null) {
            dto = learningUnitService.getLearningUnitByIdWithAuth(id, userId);
        } else {
            dto = learningUnitService.getLearningUnitById(id);
        }
        return ApiResponse.success(dto, "Fetched learning unit details");
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
    public ApiResponse<List<LearningUnitDto>> getLearningUnitsByTypeLevel(@PathVariable int level) {
        List<LearningUnitDto> list = learningUnitService.getLearningUnitsByTypeLevel(level);
        return ApiResponse.success(list, "Fetched learning units by type level");
    }

    @GetMapping("/count/{id}")
    public ApiResponse<Long> countLearningUnitById(@PathVariable UUID id) {
        Long count = learningUnitService.countByLearningUnitId(id);
        return ApiResponse.success(count, "Counted learning units successfully");
    }

    @GetMapping("/courses/completed/{userId}")
    public ApiResponse<List<LearningUnitChildDto>> getCompletedCourses(@PathVariable UUID userId) {
        List<LearningUnitChildDto> completed = learningUnitService.getAllExerciseStatisticLUWithUserId(userId).stream()
                .filter(c -> c.numberOfComplete() > 0 && c.numberOfComplete() >= c.numberOfExercise())
                .toList();
        return ApiResponse.success(completed, "Fetched completed courses");
    }

    @GetMapping("/courses/incompleted/{userId}")
    public ApiResponse<List<LearningUnitChildDto>> getIncompletedCourses(@PathVariable UUID userId) {
        List<LearningUnitChildDto> incompleted = learningUnitService.getAllExerciseStatisticLUWithUserId(userId).stream()
                .filter(c -> c.numberOfComplete() < c.numberOfExercise())
                .toList();
        return ApiResponse.success(incompleted, "Fetched incompleted courses");
    }

    @GetMapping("/{id}/exercises")
    public ApiResponse<List<ExerciseWithAnswerDto>> getExerciseIdsByCourseId(@PathVariable UUID id) {
        List<ExerciseWithAnswerDto> exerciseDtos = learningUnitService.getExerciseIdsByLearningUnitId(id);
        return ApiResponse.success(exerciseDtos, "Fetched exercises for the course");
    }

    @PostMapping("/generate/categories/{originalExCateId}")
    public ApiResponse<List<CreateExerciseCategoryDto>> defineExerciseCategories(@PathVariable UUID originalExCateId) {
        List<CreateExerciseCategoryDto> categories = aIService.defineExerciseCategory(originalExCateId);
        return ApiResponse.success(categories, "Exercise categories defined successfully");
    }

    @PostMapping("/generate/exercises")
    public ApiResponse<List<ExerciseWithAnswerDto>> generateExercises(@RequestParam UUID lessonId, @RequestParam List<CreateExerciseCategoryDto> categories) {
        List exercises = aIService.generateExercises(lessonId, categories);
        return ApiResponse.success(exercises, "Exercises generated successfully");
    }
}
