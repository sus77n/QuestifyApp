package com.example.questifyapp.controller;

import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.entity.Lesson;
import com.example.questifyapp.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {
    @Autowired
    private LessonService lessonService;

    @GetMapping("/{lessonId}/exercises")
    public ResponseEntity<List<Exercise>> getListExercise(@PathVariable Long lessonId) {
        List<Exercise> exercises = lessonService.getExercisesByLessonId(lessonId);
        return ResponseEntity.ok(exercises);
    }
}
