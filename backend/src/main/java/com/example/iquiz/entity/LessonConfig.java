package com.example.iquiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "lesson_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonConfig {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "lesson_id")
    private LearningUnit lesson;

    @Column(name = "questions_per_attempt", nullable = false)
    private int questionsPerAttempt;

    @Column(name = "pass_threshold", nullable = false)
    private int passThreshold;

    @Column(name = "no_repeat_scope", nullable = false)
    private boolean noRepeatScope = true;
}
