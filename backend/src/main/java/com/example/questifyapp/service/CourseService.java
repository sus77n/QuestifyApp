package com.example.questifyapp.service;

import com.example.questifyapp.dto.CourseDTO;
import com.example.questifyapp.dto.LearningUnitDto;
import com.example.questifyapp.entity.Course;
import com.example.questifyapp.entity.LearningUnit;
import com.example.questifyapp.mapper.CourseMapper;
import com.example.questifyapp.mapper.LearningUnitMapper;
import com.example.questifyapp.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream().map(CourseMapper::tDto).toList();
    }

    public CourseDTO getCourseById(Long id) {
        return CourseMapper.tDto(courseRepository.findById(id).get());
    }

    public List<LearningUnitDto> getChaptersByCourseId(Long id) {
        Course course = courseRepository.findById(id).get();
        List<LearningUnitDto> learningUnits = course.getLearningUnits()
                .stream().map(LearningUnitMapper::toDto).toList();
        return learningUnits;
    }

    public Course getCourseByCourseCode(String courseCode) {
        return courseRepository.findByCode(courseCode);
    }

    public List<CourseDTO> searchCourses(String searchTerm) {
        return courseRepository.searchCoursesByNameOrCode(searchTerm)
                .stream().map(CourseMapper::tDto).toList();
    }

    public int countTotalExercisesByCourseId(Long courseId) {
        Course course = courseRepository.findById(courseId).get();
        if (course == null || course.getLearningUnits() == null) {
            return 0;
        }

        int count = 0;

        for (LearningUnit chapter : course.getLearningUnits()) {
            for (LearningUnit lesson : chapter.getChildUnits()) {
                count += lesson.getChildUnits().size();
            }
        }

        return count;
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }
    
    public void addCourse(Course course) {
        courseRepository.save(course);
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
