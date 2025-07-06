package com.example.questifyapp.controller;

import com.example.questifyapp.dto.CourseDTO;
import com.example.questifyapp.entity.Course;
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

    // Basic CRUD Operations

    /**
     * Get all courses
     */
    @GetMapping("")
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses.stream()
                .map(CourseMapper::tDto)
                .toList());
    }

    /**
     * Get course by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(CourseMapper.tDto(course));
    }

    /**
     * Create a new course
     */
    @PostMapping("")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        try {
            Course course = CourseMapper.toEntity(courseDTO);
            Course createdCourse = courseService.createCourse(course);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CourseMapper.tDto(createdCourse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Update an existing course
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseDTO courseDTO) {

        if (!courseService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            Course course = CourseMapper.toEntity(courseDTO);
            course.setId(id);
            Course updatedCourse = courseService.updateCourse(course);
            return ResponseEntity.ok(CourseMapper.tDto(updatedCourse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Delete course by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable Long id) {
        if (!courseService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        try {
            courseService.deleteCourse(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Course deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete course");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Search and Filter Operations

    /**
     * Search courses by name or code
     */
    @GetMapping("/search")
    public ResponseEntity<List<CourseDTO>> searchCourses(@RequestParam String searchTerm) {
        List<Course> courses = courseService.searchCourses(searchTerm);
        if (courses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(courses.stream()
                        .map(CourseMapper::tDto)
                .toList());
    }

    /**
     * Get course by course code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<CourseDTO> getCourseByCode(@PathVariable String code) {
        Course course = courseService.getCourseByCourseCode(code);
        if (course == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(CourseMapper.tDto(course));
    }

    // Statistics and Utility Operations

    /**
     * Get total exercises count for a course
     */
    @GetMapping("/{courseId}/total/exercises")
    public ResponseEntity<Map<String, Integer>> getTotalExercises(@PathVariable Long courseId) {
        int total = courseService.countTotalExercisesByCourseId(courseId);

        Map<String, Integer> response = new HashMap<>();
        response.put("total", total);
        return ResponseEntity.ok(response);
    }

    /**
     * Get total courses count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalCoursesCount() {
        long count = courseService.countTotalCourses();
        Map<String, Long> response = new HashMap<>();
        response.put("total", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Check if course exists by code
     */
    @GetMapping("/exists/code/{code}")
    public ResponseEntity<Map<String, Boolean>> existsByCode(@PathVariable String code) {
        boolean exists = courseService.existsByCode(code);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    /**
     * Check if course exists by name
     */
    @GetMapping("/exists/name/{name}")
    public ResponseEntity<Map<String, Boolean>> existsByName(@PathVariable String name) {
        boolean exists = courseService.existsByName(name);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}
