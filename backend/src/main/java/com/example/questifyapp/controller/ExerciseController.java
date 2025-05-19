package com.example.questifyapp.controller;

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
@RequestMapping("/api/exercise")
public class ExerciseController {
    @Autowired
    private ExerciseService exerciseService;

    @GetMapping("/byLessonId/{lessonId}")
    public ResponseEntity<List<Exercise>> getListExercise(@PathVariable Long lessonId) {
        List<Exercise> exercises = exerciseService.getExercisesByLessonId(lessonId);
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExercise(@PathVariable Long id) {
        Exercise exercise = exerciseService.getExerciseById(id);
        if (exercise == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(exercise);
    }
    @PostMapping("/getOptions")
    public ResponseEntity<List<Option>> getOptions(@RequestBody Map<String, Integer> body) {
        Integer exerciseId = body.get("id");
        List<Option> options = exerciseService.getOptionsByExerciseId(exerciseId);
        return ResponseEntity.ok(options);
    }

}
