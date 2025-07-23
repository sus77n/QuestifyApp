package com.example.questifyapp.controller;

import com.example.questifyapp.dto.ExerciseTypeDto;
import com.example.questifyapp.service.ExerciseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercise-type")
public class ExerciseTypeController {
    @Autowired
    private ExerciseTypeService exerciseTypeService;

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
        return ResponseEntity.ok(exerciseTypeService.saveExerciseType(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseTypeDto> updateExerciseType(@RequestBody ExerciseTypeDto dto, @PathVariable Long id) {
        return ResponseEntity.ok(exerciseTypeService.updateExerciseType(id, dto));
    }
}
