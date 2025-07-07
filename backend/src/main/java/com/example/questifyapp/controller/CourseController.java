package com.example.questifyapp.controller;

import com.example.questifyapp.dto.CourseDTO;
import com.example.questifyapp.dto.LearningUnitDto;
import com.example.questifyapp.entity.Course;
import com.example.questifyapp.entity.LearningUnit;
import com.example.questifyapp.mapper.CourseMapper;
import com.example.questifyapp.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/{id}/chapters")
    public ResponseEntity<List<LearningUnitDto>> getChapterByCourseId(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getChaptersByCourseId(id));
    }

    @GetMapping("/{id}/count")
    public ResponseEntity<Integer> getCourseCount(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.countTotalExercisesByCourseId(id));
    }


}
