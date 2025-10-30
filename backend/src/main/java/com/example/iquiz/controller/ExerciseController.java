package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.dto.exercise.ExerciseRequestDto;
import com.example.iquiz.dto.exercise.ExerciseResponseDto;
import com.example.iquiz.dto.option.OptionResponseDto;
import com.example.iquiz.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping("/{id}")
    public ApiResponse<ExerciseResponseDto> getExercise(@PathVariable Long id) {
        ExerciseResponseDto exercise = exerciseService.getExerciseById(id);
        return ApiResponse.success(exercise, "Exercise fetched successfully");
    }

    @GetMapping("/{exerciseId}/options")
    public ApiResponse<List<OptionResponseDto>> getOptionsForExercise(@PathVariable Long exerciseId) {
        List<OptionResponseDto> options = exerciseService.getOptionsByExerciseId(exerciseId);
        return ApiResponse.success(options, "Exercise options fetched successfully");
    }

    @GetMapping
    public ApiResponse<List<ExerciseResponseDto>> getExercises(
            @RequestParam(required = false) Long lessonId,
            @RequestParam(required = false) Long typeId
    ) {
        List<ExerciseResponseDto> exercises = exerciseService.getExercises(lessonId, typeId);
        return ApiResponse.success(exercises, "Exercises fetched successfully");
    }

    @PostMapping
    public ApiResponse<ExerciseResponseDto> createExercise(@RequestBody ExerciseRequestDto dto) {
        ExerciseResponseDto created = exerciseService.saveExercise(dto);
        return ApiResponse.success(created, "Exercise created successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<ExerciseResponseDto> updateExercise(
            @PathVariable Long id,
            @RequestBody ExerciseRequestDto dto
    ) {
        ExerciseResponseDto updated = exerciseService.updateExercise(id, dto);
        return ApiResponse.success(updated, "Exercise updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ApiResponse.success(null, "Exercise deleted successfully");
    }
}
