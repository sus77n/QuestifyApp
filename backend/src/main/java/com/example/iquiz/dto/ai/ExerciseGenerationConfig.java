package com.example.iquiz.dto.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExerciseGenerationConfig {
    @Builder.Default
    int multipleChoice = 10;

    @Builder.Default
    int selectMultiple = 10;

    @Builder.Default
    int trueFalse = 5;

    @Builder.Default
    int matching = 5;

    @Builder.Default
    int reordering = 5;

    @Builder.Default
    int fillInBlank = 5;

    @Builder.Default
    int shortAnswer = 5;

    public int getTotal() {
        return multipleChoice + selectMultiple + trueFalse +
                matching + reordering + fillInBlank + shortAnswer;
    }
}