package com.example.iquiz.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PromptTemplate {
    CONTEXT_HEADER("contextHeader.md"),
    DEFINE_EXERCISE_CATEGORY("defineExerciseCategory.md"),
    GENERATE_EXERCISES("generateExercises.md"),
    EXERCISE_GENERATION_CONFIG("exerciseGenerationConfig.md"),
    ESSAY_SHORTANSWER_EVALUATION("essayAndShortAnswerEvaluate.md"),
    ATTEMPT_FEEDBACK("attemptFeedback.md"),
    ;

    private final String fileName;
}