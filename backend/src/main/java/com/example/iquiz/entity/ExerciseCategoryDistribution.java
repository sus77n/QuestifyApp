package com.example.iquiz.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "exercise_category_distribution")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseCategoryDistribution {

    @EmbeddedId
    private ExerciseCategoryDistributionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lessonId")
    @JoinColumn(name = "parent_lesson_id", nullable = false)
    private LearningUnit parentLesson;

    @Column(name = "base_weight", precision = 5, scale = 2, nullable = false)
    private BigDecimal baseWeight = BigDecimal.valueOf(1.0);

    @Column(name = "min_per_attempt")
    private Integer minPerAttempt;

    @Column(name = "max_per_attempt")
    private Integer maxPerAttempt;
}