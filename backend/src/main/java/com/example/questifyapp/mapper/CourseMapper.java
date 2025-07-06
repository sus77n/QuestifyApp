package com.example.questifyapp.mapper;

import com.example.questifyapp.dto.CourseDTO;
import com.example.questifyapp.entity.Course;

public class CourseMapper {
      public static CourseDTO tDto  (Course course) {
        return new CourseDTO(course.getId(),
                course.getName(),
                course.getDescription(),
                course.getCode(),
                course.getChapters().size());
    }
    
    public static Course toEntity(CourseDTO courseDTO) {
        Course course = new Course();
        course.setId(courseDTO.id());
        course.setName(courseDTO.name());
        course.setDescription(courseDTO.description());
        course.setCode(courseDTO.code());
        return course;
    }
}
