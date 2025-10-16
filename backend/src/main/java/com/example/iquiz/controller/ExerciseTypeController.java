package com.example.iquiz.controller;

import com.example.iquiz.dto.ExerciseTypeDto;
import com.example.iquiz.service.ExerciseTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercise-types")
@RequiredArgsConstructor
public class ExerciseTypeController {

    private final ExerciseTypeService exerciseTypeService;

    @GetMapping
    public ResponseEntity<List<ExerciseTypeDto>> getAllExerciseTypes() {
        return ResponseEntity.ok(exerciseTypeService.getAllExerciseTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseTypeDto> getExerciseType(@PathVariable Long id) {
        return ResponseEntity.ok(exerciseTypeService.getExerciseTypeById(id));
    }

    @PostMapping
    public ResponseEntity<ExerciseTypeDto> addExerciseType(@RequestBody ExerciseTypeDto dto) {
        ExerciseTypeDto created = exerciseTypeService.saveExerciseType(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseTypeDto> updateExerciseType(
            @PathVariable Long id,
            @RequestBody ExerciseTypeDto dto
    ) {
        return ResponseEntity.ok(exerciseTypeService.updateExerciseType(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExerciseType(@PathVariable Long id) {
        exerciseTypeService.deleteExerciseTypeById(id);
        return ResponseEntity.noContent().build();
    }
}

