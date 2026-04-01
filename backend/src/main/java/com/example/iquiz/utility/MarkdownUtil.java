package com.example.iquiz.utility;

import com.example.iquiz.dto.ai.CategoryCompactDto;
import com.example.iquiz.dto.ai.ExerciseCompactDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.enums.PromptTemplate;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MarkdownUtil {

    public String loadPrompt(PromptTemplate name) {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/" + name.getFileName());
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load prompt: " + name, e);
        }
    }

    public String exercisesToCompactText(List<ExerciseCompactDto> exercises) {
        if (exercises == null || exercises.isEmpty()) {
            return "";
        }

        return exercises.stream()
                .map(this::exerciseToCompactLine)
                .collect(Collectors.joining("\n"));
    }


    private String exerciseToCompactLine(ExerciseCompactDto ex) {
        return String.format(
                "[%s] TYPE:%s | DIFF:%s | Q:%s | ANS:%s",
                ex.getId(),
                safe(ex.getType()),
                safe(ex.getDifficulty()),
                cleanText(ex.getQuestion()),
                cleanJson(ex.getCorrectAnswer())
        );
    }


    public String categoryWithExercisesToCompactText(CategoryCompactDto category, int index) {
        StringBuilder sb = new StringBuilder(256);

        sb.append("--- CATEGORY ").append(index).append(" ---\n")
                .append("Hierarchy: ").append(safe(category.getHierarchyPath())).append("\n")
                .append("Name: ").append(safe(category.getName()))
                .append(" [ID: ").append(category.getId()).append("]\n")
                .append("Desc: ").append(cleanText(category.getDescription())).append("\n")
                .append("Exercises:\n");

        if (category.getExercises() != null && !category.getExercises().isEmpty()) {
            sb.append(exercisesToCompactText(category.getExercises())).append("\n");
        }

        sb.append("\n");
        return sb.toString();
    }


    public String buildHierarchyPath(LearningUnit unit) {
        List<String> nodes = new ArrayList<>();

        while (unit != null) {
            nodes.add(unit.getType().getName() + ": " + unit.getName());
            unit = unit.getParent();
        }

        Collections.reverse(nodes);
        return String.join(" > ", nodes);
    }


    private String cleanText(String input) {
        return input == null ? "" : input.replace("\n", " ").trim();
    }

    private String cleanJson(String input) {
        return input == null ? "" : input.replace("\n", "").replace(" ", "").trim();
    }

    private String safe(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}
