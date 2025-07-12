package com.example.questifyapp.controller;

import com.example.questifyapp.dto.LearningUnitTypeDto;
import com.example.questifyapp.service.LearningUnitTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-unit-types")
public class LearningUnitTypeController {
    @Autowired
    private LearningUnitTypeService learningUnitTypeService;

    @GetMapping
    public ResponseEntity<List<LearningUnitTypeDto>> getLearningUnitTypes() {
        return ResponseEntity.ok(learningUnitTypeService.getLearningUnitTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningUnitTypeDto> getLearningUnitTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(learningUnitTypeService.getLearningUnitTypeById(id));
    }

    @PostMapping
    public ResponseEntity<LearningUnitTypeDto> createLearningUnitType(@RequestBody LearningUnitTypeDto learningUnitTypeDto) {
        return ResponseEntity.ok(learningUnitTypeService.saveLearningUnitType(learningUnitTypeDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LearningUnitTypeDto> updateLUT(@RequestBody LearningUnitTypeDto learningUnitTypeDto, @PathVariable Long id) {
        return ResponseEntity.ok(learningUnitTypeService.updateLearningUnitType(id, learningUnitTypeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLearningUnitType(@PathVariable Long id) {
        learningUnitTypeService.deleteLearningUnitTypeById(id);
        return ResponseEntity.ok("Learning Unit Type has been deleted");
    }
}
