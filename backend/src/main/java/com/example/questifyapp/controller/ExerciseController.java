package com.example.questifyapp.controller;

import com.example.questifyapp.dto.exercise.ExerciseRequestDto;
import com.example.questifyapp.dto.exercise.ExerciseResponseDto;
import com.example.questifyapp.dto.option.OptionResponseDto;
import com.example.questifyapp.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
    @Autowired
    private ExerciseService exerciseService;

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponseDto> getExercise(@PathVariable Long id) {
        return ResponseEntity.ok(exerciseService.getExerciseById(id));
    }

    @GetMapping("/{exerciseId}/options")
    public ResponseEntity<List<OptionResponseDto>> getOptionsForExercise(
            @PathVariable Long exerciseId) {
        return ResponseEntity.ok(exerciseService.getOptionsByExerciseId(exerciseId));
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponseDto>> getAllExercises() {
        return ResponseEntity.ok(exerciseService.getAllExercises());
    }

    @PostMapping
    public ResponseEntity<ExerciseResponseDto> createExercise(@RequestBody ExerciseRequestDto dto) {
        return ResponseEntity.ok(exerciseService.saveExercise(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponseDto> updateExercise(@RequestBody ExerciseRequestDto dto, @PathVariable Long id) {
        return ResponseEntity.ok(exerciseService.updateExercise(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.ok("Exercise has been deleted");
    }
}
