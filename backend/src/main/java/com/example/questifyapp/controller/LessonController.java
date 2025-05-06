package com.example.questifyapp.controller;

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
@RequestMapping("/api/lesson")
public class LessonController {
    @Autowired
    private LessonService lessonService;

    @GetMapping("/{chapterId}")
    public ResponseEntity<List<Lesson>> getLessonByChapterId(@PathVariable("chapterId") Long chapterId) {
        List<Lesson> lessons = lessonService.getLessonsByChapterId(chapterId);
        return ResponseEntity.ok(lessons);
    }
}
