package com.example.iquiz.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonTypeDistributionId implements Serializable {
    private Long lessonId;
    private Long exerciseTypeId;
}
