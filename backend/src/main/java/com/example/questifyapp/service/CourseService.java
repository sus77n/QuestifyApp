package com.example.questifyapp.service;

import com.example.questifyapp.entity.Course;
import com.example.questifyapp.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    public Course getCourseByCourseCode(String courseCode) {
        return courseRepository.findByCode(courseCode);
    }

    public List<Course> searchCourses(String searchTerm) {
        return courseRepository.searchCoursesByNameOrCode(searchTerm);
    }

    public int countTotalExercisesByCourseId(Long courseId) {
        Course course = getCourseById(courseId);
        if (course == null || course.getLearningUnits() == null) {
            return 0;
        }

        int count = 0;
        // Count exercises in learning units
        // This would depend on your Exercise entity relationship
        // For now, returning 0 as placeholder
        return count;
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return courseRepository.existsById(id);
    }

    public boolean existsByCode(String code) {
        return courseRepository.existsByCode(code);
    }

    public boolean existsByName(String name) {
        return courseRepository.existsByName(name);
    }

    public long countTotalCourses() {
        return courseRepository.count();
    }
}
