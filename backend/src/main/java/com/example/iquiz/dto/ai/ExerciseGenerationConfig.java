package com.example.iquiz.dto.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExerciseGenerationConfig {
    int multipleChoice = 10;
    int selectMultiple = 10;
    int trueFalse = 5;
    int matching = 5;
    int reordering = 5;
    int fillInBlank = 5;
    int shortAnswer = 5;

    public int getTotal() {
        return multipleChoice + selectMultiple + trueFalse +
                matching + reordering + fillInBlank + shortAnswer;
    }
}