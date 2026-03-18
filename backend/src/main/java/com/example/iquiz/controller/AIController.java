package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.dto.ai.ExportedCategoryDto;
import com.example.iquiz.dto.ai.GenerateExercisesRequest;
import com.example.iquiz.dto.learningUnit.CreateExerciseCategoryDto;
import com.example.iquiz.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {
    @Autowired
    private AIService aIService;

    @PostMapping("/define/categories/{originalExCateId}")
    public ApiResponse<List<CreateExerciseCategoryDto>> defineExerciseCategories(@PathVariable UUID originalExCateId) {
        List<CreateExerciseCategoryDto> categories = aIService.defineExerciseCategory(originalExCateId);
        return ApiResponse.success(categories, "Exercise categories defined successfully");
    }

    @PostMapping("/generate/exercises")
    public ApiResponse<List<ExportedCategoryDto>> generateExercises(@RequestBody GenerateExercisesRequest request) {
        List<ExportedCategoryDto> exercises = aIService.generateExercises(request);
        return ApiResponse.success(exercises, "Exercises generated successfully");
    }
}
