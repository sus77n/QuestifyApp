package com.example.iquiz.dto.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExerciseGenerationConfig {
    int multipleChoice = 10;
    int selectMultiple = 10;
    int trueFalse = 10;
    int matching = 10;
    int reordering = 10;
    int fillInBlank = 10;
    int shortAnswer = 10;

    public int getTotal() {
        return multipleChoice + selectMultiple + trueFalse +
                matching + reordering + fillInBlank + shortAnswer;
    }
}