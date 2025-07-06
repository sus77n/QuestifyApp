package com.example.questifyapp.mapper;

import com.example.questifyapp.dto.CourseDTO;
import com.example.questifyapp.entity.Course;

public class CourseMapper {

    public static CourseDTO tDto(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getCode(),
                course.getLearningUnits().stream()
                        .map(LearningUnitMapper::toDto)
                        .toList()
        );
    }

    public static Course toEntity(CourseDTO courseDTO) {
        return new Course(
                courseDTO.id(),
                courseDTO.code(),
                courseDTO.name(),
                courseDTO.description(),
                courseDTO.learningUnits().stream()
                        .map(LearningUnitMapper::toEntity)
                        .toList()
        );
    }
}
