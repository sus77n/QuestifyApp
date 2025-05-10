package com.example.questifyapp.repository;

import com.example.questifyapp.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query("SELECT c FROM Course c WHERE LOWER(c.courseName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Course> searchCoursesByNameOrCode(String searchTerm);
}
