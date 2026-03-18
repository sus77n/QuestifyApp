package com.example.iquiz.entity;
import com.example.iquiz.enums.AITaskStatus;
import com.example.iquiz.enums.AITaskType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AILog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AITaskType taskType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User actor;

    @Column(nullable = false)
    private LocalDateTime requestTimestamp;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String inputPayload;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String outputResponse;

    private String modelAI;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AITaskStatus status;
}