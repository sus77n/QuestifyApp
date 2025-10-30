package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.dto.LearningUnitTypeDto;
import com.example.iquiz.service.LearningUnitTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-unit-types")
@RequiredArgsConstructor
public class LearningUnitTypeController {

    private final LearningUnitTypeService learningUnitTypeService;

    @GetMapping
    public ApiResponse<List<LearningUnitTypeDto>> getLearningUnitTypes() {
        List<LearningUnitTypeDto> types = learningUnitTypeService.getLearningUnitTypes();
        return ApiResponse.success(types, "Fetched all learning unit types");
    }

    @GetMapping("/{id}")
    public ApiResponse<LearningUnitTypeDto> getLearningUnitTypeById(@PathVariable Long id) {
        LearningUnitTypeDto type = learningUnitTypeService.getLearningUnitTypeById(id);
        return ApiResponse.success(type, "Fetched learning unit type details");
    }

    @PostMapping
    public ApiResponse<LearningUnitTypeDto> createLearningUnitType(@Valid @RequestBody LearningUnitTypeDto dto) {
        LearningUnitTypeDto created = learningUnitTypeService.saveLearningUnitType(dto);
        return ApiResponse.success(created, "Learning unit type created successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<LearningUnitTypeDto> updateLearningUnitType(
            @PathVariable Long id,
            @Valid @RequestBody LearningUnitTypeDto dto
    ) {
        LearningUnitTypeDto updated = learningUnitTypeService.updateLearningUnitType(id, dto);
        return ApiResponse.success(updated, "Learning unit type updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteLearningUnitType(@PathVariable Long id) {
        learningUnitTypeService.deleteLearningUnitTypeById(id);
        return ApiResponse.success(null, "Learning unit type deleted successfully");
    }
}
