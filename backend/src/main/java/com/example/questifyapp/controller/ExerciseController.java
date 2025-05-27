package com.example.questifyapp.controller;

import com.example.questifyapp.dto.ExerciseDTO;
import com.example.questifyapp.dto.OptionDTO;
import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.entity.Option;
import com.example.questifyapp.service.ExerciseService;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
    @Autowired
    private ExerciseService exerciseService;

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDTO> getExercise(@PathVariable Long id) {
        Exercise exercise = exerciseService.getExerciseById(id);
        if (exercise == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ExerciseDTO.fromEntity(exercise));
    }

    @GetMapping("/{exerciseId}/options")
    public ResponseEntity<List<OptionDTO>> getOptionsForExercise(
            @PathVariable Long exerciseId) {
        List<Option> options = exerciseService.getExerciseById(exerciseId).getOptions();
        List<OptionDTO> optionDTOs = options.stream()
                .map(OptionDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(optionDTOs);
    }

}
