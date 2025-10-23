package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.dto.learningUnit.CourseDto;
import com.example.iquiz.dto.learningUnit.LearningUnitDto;
import com.example.iquiz.dto.learningUnit.LearningUnitTreeDto;
import com.example.iquiz.service.LearningUnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-units")
@RequiredArgsConstructor
public class LearningUnitController {

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

    @GetMapping("/{id}")
    public ApiResponse<LearningUnitDto> getLearningUnitById(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId
    ) {
        LearningUnitDto dto = learningUnitService.getLearningUnitById(id, userId);
        return ApiResponse.success(dto, "Fetched learning unit details");
    }

    @GetMapping("/getLearningUnitWithChildren/{id}")
    public ApiResponse<LearningUnitTreeDto> getLearningUnitWithChildren(@PathVariable Long id) {
        LearningUnitTreeDto tree = learningUnitService.getLearningUnitWithChildren(id);
        return ApiResponse.success(tree, "Fetched learning unit with children");
    }

    @PutMapping("/{id}")
    public ApiResponse<LearningUnitDto> updateLearningUnit(
            @PathVariable Long id,
            @Valid @RequestBody LearningUnitDto dto
    ) {
        LearningUnitDto updated = learningUnitService.updateLearningUnit(id, dto);
        return ApiResponse.success(updated, "Learning unit updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteLearningUnit(@PathVariable Long id) {
        learningUnitService.deleteLearningUnit(id);
        return ApiResponse.success(null, "Learning unit deleted successfully");
    }

    @GetMapping("/type/level/{level}")
    public ApiResponse<List<LearningUnitDto>> getLearningUnitsByTypeLevel(@PathVariable int level) {
        List<LearningUnitDto> list = learningUnitService.getLearningUnitsByTypeLevel(level);
        return ApiResponse.success(list, "Fetched learning units by type level");
    }

    @GetMapping("/count/{id}")
    public ApiResponse<Long> countLearningUnitById(@PathVariable Long id) {
        Long count = learningUnitService.countByLearningUnitId(id);
        return ApiResponse.success(count, "Counted learning units successfully");
    }

    @GetMapping("/courses/completed/{userId}")
    public ApiResponse<List<CourseDto>> getCompletedCourses(@PathVariable Long userId) {
        List<CourseDto> completed = learningUnitService.getAllCoursesWithUserId(userId).stream()
                .filter(c -> c.completedExercises() != null &&
                        c.completedExercises().equals(c.totalOfExercise()))
                .toList();
        return ApiResponse.success(completed, "Fetched completed courses");
    }

    @GetMapping("/courses/incompleted/{userId}")
    public ApiResponse<List<CourseDto>> getIncompletedCourses(@PathVariable Long userId) {
        List<CourseDto> incompleted = learningUnitService.getAllCoursesWithUserId(userId).stream()
                .filter(c -> c.completedExercises() != null &&
                        c.completedExercises() < c.totalOfExercise())
                .toList();
        return ApiResponse.success(incompleted, "Fetched incompleted courses");
    }
}
