package com.example.iquiz.controller;

import com.example.iquiz.dto.LearningUnitTypeDto;
import com.example.iquiz.service.LearningUnitTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-unit-types")
@RequiredArgsConstructor
public class LearningUnitTypeController {

    private final LearningUnitTypeService learningUnitTypeService;

    @GetMapping
    public ResponseEntity<List<LearningUnitTypeDto>> getLearningUnitTypes() {
        return ResponseEntity.ok(learningUnitTypeService.getLearningUnitTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningUnitTypeDto> getLearningUnitTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(learningUnitTypeService.getLearningUnitTypeById(id));
    }

    @PostMapping
    public ResponseEntity<LearningUnitTypeDto> createLearningUnitType(@RequestBody LearningUnitTypeDto dto) {
        LearningUnitTypeDto created = learningUnitTypeService.saveLearningUnitType(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LearningUnitTypeDto> updateLearningUnitType(
            @PathVariable Long id,
            @RequestBody LearningUnitTypeDto dto
    ) {
        return ResponseEntity.ok(learningUnitTypeService.updateLearningUnitType(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLearningUnitType(@PathVariable Long id) {
        learningUnitTypeService.deleteLearningUnitTypeById(id);
        return ResponseEntity.noContent().build();
    }
}
