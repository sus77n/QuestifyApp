package com.example.questifyapp.controller;

import com.example.questifyapp.dto.CourseDTO;
import com.example.questifyapp.entity.Course;
import com.example.questifyapp.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("")
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses.stream()
                .map(CourseDTO::fromEntity)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Integer id) {
        Course course = courseService.getCourseById(id);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(CourseDTO.fromEntity(course));
    }

    @GetMapping("/{courseId}/chapters")
    public ResponseEntity<List<ChapterDTO>> getAllChapters(@PathVariable Integer courseId) {
        List<Chapter> chapters = courseService.getChaptersByCourseId(courseId);
        return ResponseEntity.ok(chapters.stream()
                .map(ChapterDTO::fromEntity)
                .toList());
    }

    @GetMapping("/search")
    public ResponseEntity<List<CourseDTO>> searchCourses(@RequestParam String searchTerm) {
        List<Course> courses = courseService.searchCourses(searchTerm);
        if (courses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(courses.stream()
                        .map(CourseDTO::fromEntity)
                .toList());
    }

    @GetMapping("/{courseId}/total/exercises")
    public ResponseEntity<Map<String, Integer>> getTotalExercises(@PathVariable Integer courseId) {
        int total = courseService.countTotalExercisesByCourseId(courseId);

        Map<String, Integer> response = new HashMap<>();
        response.put("total", total);
        return ResponseEntity.ok(response);
    }

}
