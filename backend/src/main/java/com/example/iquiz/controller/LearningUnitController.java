package com.example.iquiz.controller;

import com.example.iquiz.dto.learningUnit.CourseDto;
import com.example.iquiz.dto.learningUnit.LearningUnitDto;
import com.example.iquiz.dto.learningUnit.LearningUnitTreeDto;
import com.example.iquiz.service.LearningUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-units")
@RequiredArgsConstructor
public class LearningUnitController {

    private final LearningUnitService learningUnitService;

    @GetMapping
    public ResponseEntity<List<LearningUnitDto>> getAll() {
        return ResponseEntity.ok(learningUnitService.getAllLearningUnits());
    }

    @PostMapping
    public ResponseEntity<LearningUnitDto> createLearningUnit(@RequestBody LearningUnitDto dto) {
        LearningUnitDto created = learningUnitService.saveLearningUnit(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningUnitDto> getLearningUnitById(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId
    ) {
        return ResponseEntity.ok(learningUnitService.getLearningUnitById(id, userId));
    }

    @GetMapping("/getLearningUnitWithChildren/{id}")
    public ResponseEntity<LearningUnitTreeDto> getLearningUnitWithChildren(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(learningUnitService.getLearningUnitWithChildren(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LearningUnitDto> updateLearningUnit(
            @PathVariable Long id,
            @RequestBody LearningUnitDto dto
    ) {
        return ResponseEntity.ok(learningUnitService.updateLearningUnit(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLearningUnit(@PathVariable Long id) {
        learningUnitService.deleteLearningUnit(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/level/{level}")
    public ResponseEntity<List<LearningUnitDto>> getLearningUnitsByTypeLevel(@PathVariable int level) {
        return ResponseEntity.ok(learningUnitService.getLearningUnitsByTypeLevel(level));
    }

    @GetMapping("/count/{id}")
    public ResponseEntity<Long> countLearningUnitById(@PathVariable Long id) {
        return ResponseEntity.ok(learningUnitService.countByLearningUnitId(id));
    }

    @GetMapping("/courses/completed/{userId}")
    public ResponseEntity<List<CourseDto>> getCompletedCourses(@PathVariable Long userId) {
        return ResponseEntity.ok(
                learningUnitService.getAllCoursesWithUserId(userId).stream()
                        .filter(c -> c.completedExercises() != null
                                && c.completedExercises() == c.totalOfExercise())
                        .toList()
        );
    }

    @GetMapping("/courses/incompleted/{userId}")
    public ResponseEntity<List<CourseDto>> getIncompletedCourses(@PathVariable Long userId) {
        return ResponseEntity.ok(
                learningUnitService.getAllCoursesWithUserId(userId).stream()
                        .filter(c -> c.completedExercises() != null
                                && c.completedExercises() < c.totalOfExercise())
                        .toList()
        );
    }
}
