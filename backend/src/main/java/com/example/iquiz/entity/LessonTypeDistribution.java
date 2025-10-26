package com.example.iquiz.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lesson_type_distribution")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonTypeDistribution {

    @EmbeddedId
    private LessonTypeDistributionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lessonId")
    @JoinColumn(name = "lesson_id", nullable = false)
    private LearningUnit lesson;

    @Column(name = "base_weight", precision = 5, scale = 2, nullable = false)
    private BigDecimal baseWeight = BigDecimal.valueOf(1.0);

    @Column(name = "min_per_attempt")
    private Integer minPerAttempt;

    @Column(name = "max_per_attempt")
    private Integer maxPerAttempt;
}