package com.example.iquiz.controller;

import com.example.iquiz.dto.exercise.ExerciseRequestDto;
import com.example.iquiz.dto.exercise.ExerciseResponseDto;
import com.example.iquiz.dto.option.OptionResponseDto;
import com.example.iquiz.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponseDto> getExercise(@PathVariable Long id) {
        return ResponseEntity.ok(exerciseService.getExerciseById(id));
    }

    @GetMapping("/{exerciseId}/options")
    public ResponseEntity<List<OptionResponseDto>> getOptionsForExercise(@PathVariable Long exerciseId) {
        return ResponseEntity.ok(exerciseService.getOptionsByExerciseId(exerciseId));
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponseDto>> getExercises(
            @RequestParam(required = false) Long lessonId,
            @RequestParam(required = false) Long typeId
    ) {
        return ResponseEntity.ok(exerciseService.getExercises(lessonId, typeId));
    }

    @PostMapping
    public ResponseEntity<ExerciseResponseDto> createExercise(@RequestBody ExerciseRequestDto dto) {
        return ResponseEntity.ok(exerciseService.saveExercise(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponseDto> updateExercise(
            @PathVariable Long id,
            @RequestBody ExerciseRequestDto dto
    ) {
        return ResponseEntity.ok(exerciseService.updateExercise(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}

