package com.example.iquiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_exercise_ratings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "exercise_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserExerciseRating {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(nullable = false)
    private double rating = 1000.0;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Version
    private Long version;
}
