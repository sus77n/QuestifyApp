package com.example.iquiz.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_mastery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMastery {

    @EmbeddedId
    private UserMasteryId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lessonId")
    @JoinColumn(name = "lesson_id", nullable = false)
    private LearningUnit lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("exerciseTypeId")
    @JoinColumn(name = "exercise_type_id", nullable = false)
    private ExerciseCategory exerciseCategory;

    @Column(name = "accuracy", nullable = false)
    private double accuracy = 0.0;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount = 0;

    @Column(name = "correct_count", nullable = false)
    private int correctCount = 0;
}
