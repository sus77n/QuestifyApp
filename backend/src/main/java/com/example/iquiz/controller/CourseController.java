package com.example.iquiz.controller;

import com.example.iquiz.dto.ApiResponse;
import com.example.iquiz.dto.learningUnit.CourseDto;
import com.example.iquiz.service.learningUnit.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public ApiResponse<List<CourseDto>> getAllCourses() {
        return ApiResponse.success(
                courseService.getAllCoursesWithAuth(),
                "Fetched all courses successfully"
        );
    }

    @PostMapping
    public ApiResponse<CourseDto> createLearningUnit(@Valid @RequestBody CourseDto dto) {
        CourseDto created = courseService.saveCourse(dto);
        return ApiResponse.success(created, "Course created successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<CourseDto> updateLearningUnit(@PathVariable("id") UUID id, @Valid @RequestBody CourseDto dto) {
        CourseDto updated = courseService.updateCourse(id, dto);
        return ApiResponse.success(updated, "Course updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteLearningUnit(@PathVariable("id") String courseId) {
        courseService.deleteCourse(java.util.UUID.fromString(courseId));
        return ApiResponse.success(null, "Course deleted successfully");
    }
}
