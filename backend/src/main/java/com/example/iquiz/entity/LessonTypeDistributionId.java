package com.example.iquiz.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonTypeDistributionId implements Serializable {
    private UUID lessonId;
    private UUID exerciseTypeId;
}
