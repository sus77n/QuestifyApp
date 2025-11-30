package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.dto.exercise.ExerciseRequestDto;
import com.example.iquiz.dto.exercise.ExerciseResponseDto;
import com.example.iquiz.dto.answer.OptionDto;
import com.example.iquiz.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping("/{id}")
    public ApiResponse<ExerciseResponseDto> getExercise(@PathVariable UUID id) {
        ExerciseResponseDto exercise = exerciseService.getExerciseById(id);
        return ApiResponse.success(exercise, "Exercise fetched successfully");
    }

    @GetMapping("/{exerciseId}/options")
    public ApiResponse<List<OptionDto>> getOptionsForExercise(@PathVariable UUID exerciseId) {
        List<OptionDto> options = exerciseService.getOptionsByExerciseId(exerciseId);
        return ApiResponse.success(options, "Exercise options fetched successfully");
    }

    @GetMapping
    public ApiResponse<List<ExerciseResponseDto>> getExercises(
            @RequestParam(required = false) UUID lessonId,
            @RequestParam(required = false) UUID typeId
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
            @PathVariable UUID id,
            @RequestBody ExerciseRequestDto dto
    ) {
        ExerciseResponseDto updated = exerciseService.updateExercise(id, dto);
        return ApiResponse.success(updated, "Exercise updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteExercise(@PathVariable UUID id) {
        exerciseService.deleteExercise(id);
        return ApiResponse.success(null, "Exercise deleted successfully");
    }
}
