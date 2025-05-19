package com.example.questifyapp.controller;

import com.example.questifyapp.entity.Course;
import com.example.questifyapp.repository.CourseRepository;
import com.example.questifyapp.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("")
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Integer id) {
        Course course = courseService.getCourseById(id);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(course);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String searchTerm) {
        List<Course> courses = courseService.searchCourses(searchTerm);
        if (courses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/totalExercise")
    public ResponseEntity<Map<String, Integer>> getTotalExercises(@RequestBody Map<String, Integer> body) {
        Integer courseId = body.get("id");

        int total = courseService.countTotalExercisesByCourseId(courseId);

        Map<String, Integer> response = new HashMap<>();
        response.put("total", total);
        return ResponseEntity.ok(response);
    }

}
