package com.example.iquiz.controller;

import com.example.iquiz.dto.ExerciseTypeDto;
import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.service.ExerciseTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercise-types")
@RequiredArgsConstructor
public class ExerciseTypeController {

    private final ExerciseTypeService exerciseTypeService;

    @GetMapping
    public ApiResponse<List<ExerciseTypeDto>> getAllExerciseTypes() {
        List<ExerciseTypeDto> types = exerciseTypeService.getAllExerciseTypes();
        return ApiResponse.success(types, "Fetched all exercise types");
    }

    @GetMapping("/{id}")
    public ApiResponse<ExerciseTypeDto> getExerciseType(@PathVariable Long id) {
        ExerciseTypeDto type = exerciseTypeService.getExerciseTypeById(id);
        return ApiResponse.success(type, "Fetched exercise type");
    }

    @PostMapping
    public ApiResponse<ExerciseTypeDto> addExerciseType(@Valid @RequestBody ExerciseTypeDto dto) {
        ExerciseTypeDto created = exerciseTypeService.saveExerciseType(dto);
        return ApiResponse.success(created, "Exercise type created successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<ExerciseTypeDto> updateExerciseType(
            @PathVariable Long id,
            @Valid @RequestBody ExerciseTypeDto dto
    ) {
        ExerciseTypeDto updated = exerciseTypeService.updateExerciseType(id, dto);
        return ApiResponse.success(updated, "Exercise type updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteExerciseType(@PathVariable Long id) {
        exerciseTypeService.deleteExerciseTypeById(id);
        return ApiResponse.success(null, "Exercise type deleted successfully");
    }
}
