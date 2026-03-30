package com.example.iquiz.dto;

import com.example.iquiz.enums.UserProgress;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProgressDTO {
    private UUID id;
    private UUID attemptId;
    private String userFullName;
    private UUID courseId;
    private String courseName;
    private String courseCode;
    private int attemptCount;
    private int completedExercises;
    private int totalExercises;
    private UserProgress status;
    private double progressPercent;
    private double bestScore;
    private LocalDateTime lastActivityAt;
}
