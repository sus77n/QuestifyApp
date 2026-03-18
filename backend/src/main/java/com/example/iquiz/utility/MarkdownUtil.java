package com.example.iquiz.utility;

import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.enums.PromptTemplate;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

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

    public StringBuilder exerciseToMarkdown(Exercise ex, int index) {
        StringBuilder exerciseBlock = new StringBuilder();
        exerciseBlock.append("## Exercise ")
                .append(index)
                .append("\n\n");

        exerciseBlock.append("- ID: `").append(ex.getId()).append("`\n");
        exerciseBlock.append("- Type: `").append(ex.getType()).append("`\n");
        exerciseBlock.append("- Difficulty: `").append(ex.getDifficulty()).append("`\n\n");
        exerciseBlock.append("**Question:**\n\n");
        exerciseBlock.append(ex.getQuestion()).append("\n\n");

        if (ex.getCorrectAnswerJson() != null && !ex.getCorrectAnswerJson().isBlank()) {
            exerciseBlock.append("**CorrectAnswersJson (raw):**\n\n");
            exerciseBlock.append(ex.getCorrectAnswerJson()).append("\n\n");
        }
        return exerciseBlock;
    }

    public String exercisesToCompactText(List<Exercise> exercises) {
        StringBuilder exercisesBlock = new StringBuilder();
        for (Exercise ex : exercises) {
            exercisesBlock.append(exerciseToCompactLine(ex)).append("\n");
        }
        return exercisesBlock.toString();
    }

    private String exerciseToCompactLine(Exercise ex) {
        String cleanQuestion = "";
        if (ex.getQuestion() != null) {
            cleanQuestion = ex.getQuestion().replace("\n", " ").trim();
        }

        String cleanAnswer = "";
        if (ex.getCorrectAnswerJson() != null && !ex.getCorrectAnswerJson().isBlank()) {
            cleanAnswer = ex.getCorrectAnswerJson().replace("\n", "").replace(" ", "").trim();
        }

        return String.format("[%s] TYPE:%s | DIFF:%s | Q:%s | ANS:%s",
                ex.getId(),
                ex.getType(),
                ex.getDifficulty(),
                cleanQuestion,
                cleanAnswer
        );
    }

    public String categoryWithExercisesToCompactText(LearningUnit category, int index) {
        StringBuilder categoryBlock = new StringBuilder();

        categoryBlock.append(String.format("--- CATEGORY %d ---\n", index));

        categoryBlock.append(getAncestorPathText(category));

        categoryBlock.append(String.format("Name: %s [ID: %s]\n", category.getName(), category.getId()));
        String cleanDesc = category.getDescription() != null ? category.getDescription().replace("\n", " ").trim() : "";
        categoryBlock.append("Desc: ").append(cleanDesc).append("\n");

        categoryBlock.append("Exercises:\n");
        if (category.getExercises() != null) {
            for (Exercise ex : category.getExercises()) {
                categoryBlock.append(exerciseToCompactLine(ex)).append("\n");
            }
        }
        categoryBlock.append("\n");

        return categoryBlock.toString();
    }

    public String getAncestorPathText(LearningUnit unit) {
        StringBuilder path = new StringBuilder();
        LearningUnit current = unit;

        while (current != null) {
            String node = current.getType().getName() + ": " + current.getName();

            if (path.length() > 0) {
                path.insert(0, " > ");
            }

            path.insert(0, node);
            current = current.getParent();
        }

        return "Hierarchy: " + path.toString() + "\n";
    }
}
