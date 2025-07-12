package com.example.questifyapp.controller;

import com.example.questifyapp.dto.learningUnit.LearningUnitDto;
import com.example.questifyapp.service.LearningUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-units")
public class LearningUnitController {

    @Autowired
    private LearningUnitService learningUnitService;

    @GetMapping
    public ResponseEntity<List<LearningUnitDto>> getAll() {
        return ResponseEntity.ok(learningUnitService.getAllLearningUnitTypes());
    }

    @PostMapping
    public ResponseEntity<LearningUnitDto> createLearningUnit(@RequestBody LearningUnitDto learningUnitDto) {
        LearningUnitDto created = learningUnitService.saveLearningUnit(learningUnitDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningUnitDto> getLearningUnitById(@PathVariable Long id) {
        LearningUnitDto learningUnitDto = learningUnitService.getLearningUnitById(id);
        return ResponseEntity.ok(learningUnitDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LearningUnitDto> updateLearningUnit(@PathVariable Long id, @RequestBody LearningUnitDto learningUnitDto) {
        LearningUnitDto updated = learningUnitService.updateLearningUnit(id, learningUnitDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/type/level/{level}")
    public ResponseEntity<List<LearningUnitDto>> getLearningUnitsByTypeLevel(@PathVariable int level) {
        return ResponseEntity.ok(learningUnitService.getLearningUnitsByTypeLevel(level));
    }
}
