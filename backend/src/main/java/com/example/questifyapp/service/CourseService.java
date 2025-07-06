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

    public Course getCourseById(int id) {
        return courseRepository.findById(id).orElse(null);
    }

    public Course getCourseByCourseCode(String courseCode) {
        List<Course> courses = getAllCourses();

        for (Course course : courses) {
            if (course.getCode().equalsIgnoreCase(courseCode)) {
                return course;
            }
        }

        return null;
    }

    public List<Course> searchCourses(String searchTerm) {
        return courseRepository.searchCoursesByNameOrCode(searchTerm);
    }

    public int countTotalExercisesByCourseId(Integer courseId) {
        Course course = getCourseById(courseId);
        if (course == null || course.getChapters() == null) {
            return 0;
        }

        int count = 0;

        for (Chapter chapter : course.getChapters()) {
            if (chapter.getLessons() == null) continue;

            for (Lesson lesson : chapter.getLessons()) {
                if (lesson.getExercises() != null) {
                    count += lesson.getExercises().size();
                }
            }
        }

        return count;
    }

    public List<Chapter> getChaptersByCourseId(Integer courseId) {
        Course course = getCourseById(courseId);
        return course.getChapters();
    }

    public void addCourse(Course course) {
        courseRepository.save(course);
    }

    public void updateCourse(Course course) {
        courseRepository.save(course);
    }

    public void deleteCourse(Integer id) {
        courseRepository.deleteById(id);
    }
}
