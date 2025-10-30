package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.entity.LessonTypeDistribution;
import com.example.iquiz.entity.LessonTypeDistributionId;
import com.example.iquiz.service.LessonTypeDistributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lesson-distributions")
@RequiredArgsConstructor
public class LessonTypeDistributionController {

    private final LessonTypeDistributionService service;

    @PostMapping
    public ApiResponse<LessonTypeDistribution> create(@RequestBody LessonTypeDistribution dist) {
        LessonTypeDistribution created = service.save(dist);
        return ApiResponse.success(created, "Lesson type distribution created successfully");
    }

    @GetMapping("/{lessonId}/{exerciseTypeId}")
    public ApiResponse<LessonTypeDistribution> getById(
            @PathVariable Long lessonId,
            @PathVariable Long exerciseTypeId
    ) {
        LessonTypeDistributionId id = new LessonTypeDistributionId(lessonId, exerciseTypeId);
        LessonTypeDistribution dist = service.findById(id);
        return ApiResponse.success(dist, "Fetched lesson type distribution");
    }

    @GetMapping("/lesson/{lessonId}")
    public ApiResponse<List<LessonTypeDistribution>> getByLesson(@PathVariable Long lessonId) {
        List<LessonTypeDistribution> list = service.findByLesson(lessonId);
        return ApiResponse.success(list, "Fetched all distributions for the lesson");
    }

    @DeleteMapping("/{lessonId}/{exerciseTypeId}")
    public ApiResponse<Void> delete(
            @PathVariable Long lessonId,
            @PathVariable Long exerciseTypeId
    ) {
        LessonTypeDistributionId id = new LessonTypeDistributionId(lessonId, exerciseTypeId);
        service.delete(id);
        return ApiResponse.success(null, "Lesson type distribution deleted successfully");
    }
}
