package com.example.iquiz.dto.learningUnit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningUnitWithStatisticDto implements LearningUnitDtoInterface {

    private UUID id;
    private String name;
    private String code;
    private String description;
    private String type;
    private int status;
    private LocalDateTime createdAt;
    private String createdBy;
    private UUID parentId;
    private List<LearningUnitWithStatisticDto> children;
    private long numberOfComplete;
    private long numberOfExercise;

}