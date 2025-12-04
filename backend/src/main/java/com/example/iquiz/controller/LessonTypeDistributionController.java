package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.entity.ExerciseCategoryDistribution;
import com.example.iquiz.entity.ExerciseCategoryDistributionId;
import com.example.iquiz.service.LessonTypeDistributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lesson-distributions")
@RequiredArgsConstructor
public class LessonTypeDistributionController {

    private final LessonTypeDistributionService service;

    @PostMapping
    public ApiResponse<ExerciseCategoryDistribution> create(@RequestBody ExerciseCategoryDistribution dist) {
        ExerciseCategoryDistribution created = service.save(dist);
        return ApiResponse.success(created, "Lesson type distribution created successfully");
    }

    @GetMapping("/{lessonId}/{exerciseTypeId}")
    public ApiResponse<ExerciseCategoryDistribution> getById(
            @PathVariable UUID lessonId,
            @PathVariable UUID exerciseTypeId
    ) {
        ExerciseCategoryDistributionId id = new ExerciseCategoryDistributionId(lessonId, exerciseTypeId);
        ExerciseCategoryDistribution dist = service.findById(id);
        return ApiResponse.success(dist, "Fetched lesson type distribution");
    }

    @GetMapping("/lesson/{lessonId}")
    public ApiResponse<List<ExerciseCategoryDistribution>> getByLesson(@PathVariable UUID lessonId) {
        List<ExerciseCategoryDistribution> list = service.findByLesson(lessonId);
        return ApiResponse.success(list, "Fetched all distributions for the lesson");
    }

    @DeleteMapping("/{lessonId}/{exerciseTypeId}")
    public ApiResponse<Void> delete(
            @PathVariable UUID lessonId,
            @PathVariable UUID exerciseTypeId
    ) {
        ExerciseCategoryDistributionId id = new ExerciseCategoryDistributionId(lessonId, exerciseTypeId);
        service.delete(id);
        return ApiResponse.success(null, "Lesson type distribution deleted successfully");
    }
}
