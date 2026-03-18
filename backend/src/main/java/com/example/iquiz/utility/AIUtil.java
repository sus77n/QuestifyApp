package com.example.iquiz.utility;

import com.example.iquiz.annotation.LogAI;
import com.example.iquiz.enums.AITaskType;
import com.example.iquiz.enums.PromptTemplate;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AIUtil {

    @Value("${gemini.model}")
    private String MODEL_NAME;
    @Autowired
    private Client client;
    @Autowired
    private MarkdownUtil markdownUtil;

    @LogAI
    public String sendPrompt(String prompt, AITaskType taskType) {
        String contextHeader = markdownUtil.loadPrompt(PromptTemplate.CONTEXT_HEADER);
        String body = """
                %s
                
                User Request:
                %s
                """.formatted(
                contextHeader, prompt);

        GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(0.1F)             // Strictness
                .topP(0.8F)                    // Focus
                .responseMimeType("application/json")
                .build();

        GenerateContentResponse response =
                client.models.generateContent(MODEL_NAME, body, config);

        return response.text().trim();
    }
}
