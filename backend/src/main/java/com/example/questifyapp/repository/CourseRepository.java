package com.example.questifyapp.repository;

import com.example.questifyapp.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Course> searchCoursesByNameOrCode(String searchTerm);
    
    // Additional repository methods
    boolean existsByCode(String code);
    boolean existsByName(String name);
    Course findByCode(String code);
}
