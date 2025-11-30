package com.example.iquiz.entity;

import com.example.iquiz.enums.ExerciseType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    /**
     * DEFAULT JSON FORMAT FOR ALL TYPES:
     * {
     *   "correctAnswers": [ ... ],  // Primary correct answers
     *   "config": { ... }           // Optional configuration
     * }
     *
     * SPECIFIC STRUCTURES:
     *
     * MULTIPLE_CHOICE/SELECT_MULTIPLE/TRUE_FALSE:
     *   { "correctAnswers": [1, 3] }  // Array of correct option IDs
     *
     * SHORT_ANSWER:
     *   { "correctAnswers": ["encapsulation"] }  // Array of acceptable answers
     *
     * MATCHING:
     *   { "correctAnswers": [{"leftId": 1, "rightId": 3}, {"leftId": 2, "rightId": 1}] }
     *
     * REORDERING:
     *   { "correctAnswers": ["1", "3", "4", "2"] }  // Correct order ids
     *
     * FILL_IN_THE_BLANK:
     *   { "correctAnswers": ["oxygen", "hydrogen"] }  // Answers for each blank in order
     */
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String correctAnswerJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ExerciseType type;

    @Column(nullable = false)
    private int difficulty;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_unit_id")
    private LearningUnit parent;

    @OneToMany(
            mappedBy = "exercise",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Answer> predefinedAnswers = new ArrayList<>();

}
