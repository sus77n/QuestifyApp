package com.example.iquiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lesson_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonConfig {

    @Id
    @Column(name = "lesson_id")
    private Long lessonId; // PK đồng thời là FK

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // dùng lesson_id làm cả PK và FK
    @JoinColumn(name = "lesson_id")
    private LearningUnit lesson;

    @Column(name = "questions_per_attempt", nullable = false)
    private int questionsPerAttempt;

    @Column(name = "pass_threshold", nullable = false)
    private int passThreshold;

    @Column(name = "no_repeat_scope", nullable = false)
    private boolean noRepeatScope = true;
}
