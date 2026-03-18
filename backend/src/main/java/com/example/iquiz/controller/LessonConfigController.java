package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.dto.LessonConfigDto;
import com.example.iquiz.service.LessonConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/lesson-configs")
@RequiredArgsConstructor
public class LessonConfigController {

    private final LessonConfigService service;

    @GetMapping("/{lessonId}")
    public ApiResponse<LessonConfigDto> getByLesson(@PathVariable UUID lessonId) {
        LessonConfigDto config = service.findByLessonId(lessonId);
        return ApiResponse.success(config, "Fetched lesson configuration");
    }

    @PutMapping("/{lessonId}")
    public ApiResponse<LessonConfigDto> update(
            @PathVariable UUID lessonId,
            @Valid @RequestBody LessonConfigDto dto
    ) {
        LessonConfigDto updated = service.update(lessonId, dto);
        return ApiResponse.success(updated, "Lesson configuration updated successfully");
    }

    @DeleteMapping("/{lessonId}")
    public ApiResponse<Void> delete(@PathVariable UUID lessonId) {
        service.delete(lessonId);
        return ApiResponse.success(null, "Lesson configuration deleted successfully");
    }
}
