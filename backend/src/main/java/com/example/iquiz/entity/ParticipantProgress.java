package com.example.iquiz.entity;

import com.example.iquiz.enums.UserProgress;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "participant_progress",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "course_id"})
        },
        indexes = {
                @Index(name = "idx_progress_user", columnList = "user_id"),
                @Index(name = "idx_progress_course", columnList = "course_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantProgress extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id")
    private LearningUnit course;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "completed_exercises", nullable = false)
    private int completedExercises;

    @Column(name = "total_exercises", nullable = false)
    private int totalExercises;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private UserProgress status;

    @Column(name = "progress_percent", precision = 5, scale = 2)
    private BigDecimal progressPercent;

    @Column(name = "best_score", precision = 5, scale = 2)
    private BigDecimal bestScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_attempt_id")
    private Attempt lastAttempt;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;
}