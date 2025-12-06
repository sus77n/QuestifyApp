package com.example.iquiz.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PromptTemplate {
    CONTEXT_HEADER("contextHeader.md"),
    DEFINE_EXERCISE_CATEGORY("defineExerciseCategory.md"),
    GENERATE_EXERCISES("generateExercises.md")
    ;

    private final String fileName;
}