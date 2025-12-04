package com.example.iquiz.service;

import com.example.iquiz.dto.learningUnit.LearningUnitChildDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.repository.ExerciseRepository;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AIService {

    @Autowired
    private Client client;

    @Value("${gemini.model}")
    private String MODEL_NAME;

    @Autowired
    private ExerciseRepository exerciseRepository;

    public String rewriteMarkdown(String inputMarkdown) {
        String prompt = """
                You are helping improve markdown for an adaptive learning website called Iquiz.

                Read the markdown document between the markers and rewrite a clearer,
                better structured version in markdown.

                RULES:
                - Output ONLY markdown.
                - Do NOT wrap result in ``` fences.
                - Preserve all important information and exercise meaning.
                - You may reorganize headings, lists, and wording to improve clarity.

                ---INPUT MARKDOWN START---
                %s
                ---INPUT MARKDOWN END---
                """.formatted(inputMarkdown);

        GenerateContentResponse response =
                client.models.generateContent(MODEL_NAME, prompt, null);

        return response.text().trim();
    }

    public List<LearningUnitChildDto> defineExerciseCategories(UUID originalExCateId) {
        List<Exercise> exercises = exerciseRepository.findByParent_Id(originalExCateId);

        return null;
    }

}
