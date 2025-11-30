package com.example.iquiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "attempt_details")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AttemptDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(precision = 5, scale = 2)
    private BigDecimal score;

    @Lob
    @Column(name = "user_answer_json", columnDefinition = "LONGTEXT")
    private String userAnswerJson;

    @Lob
    private String feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id")
    private Attempt attempt;
}