package com.example.iquiz.mapper;

import com.example.iquiz.dto.learningUnit.LearningUnitTreeDto;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.utility.LearningUnitUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LearningUnitTreeMapper {
    @Autowired
    private LearningUnitUtil learningUnitUtil;


    public LearningUnitTreeDto toTreeDto(LearningUnit course) {
        if (course == null) {
            return null;
        }
        return new LearningUnitTreeDto(
                course.getId(),
                course.getName(),
                course.getCode(),
                course.getDescription(),
                course.getType().getName(),
                course.getStatus(),
                course.getCreatedBy().getFirstName() + " " + course.getCreatedBy().getLastName(),
                learningUnitUtil.countExercises(course),
                course.getCreatedAt(),
                course.getChildren().stream()
                        .map(this::toChildDto)
                        .toList()
        );
    }

    private LearningUnitTreeDto.ChildDto toChildDto(LearningUnit chapter) {
        return new LearningUnitTreeDto.ChildDto(
                chapter.getId(),
                chapter.getName(),
                chapter.getCode(),
                chapter.getDescription(),
                chapter.getType().getName(),
                chapter.getStatus(),
                chapter.getCreatedAt(),
                chapter.getParent() != null ? chapter.getParent().getId() : null,
                chapter.getChildren().stream()
                        .map(this::toGrandChildDto)
                        .toList()
        );
    }

    private LearningUnitTreeDto.GrandChildDto toGrandChildDto(LearningUnit lesson) {
        return new LearningUnitTreeDto.GrandChildDto(
                lesson.getId(),
                lesson.getName(),
                lesson.getCode(),
                lesson.getDescription(),
                lesson.getType().getName(),
                lesson.getStatus(),
                lesson.getCreatedAt(),
                lesson.getParent() != null ? lesson.getParent().getId() : null
        );
    }
}
