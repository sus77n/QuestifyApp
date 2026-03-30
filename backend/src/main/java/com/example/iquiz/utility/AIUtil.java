package com.example.iquiz.utility;

import com.example.iquiz.annotation.LogAI;
import com.example.iquiz.enums.AITaskType;
import com.example.iquiz.enums.PromptTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();


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

    public String validationAIResponse(String json) {
        int maxRetry = 3;

        for (int i = 0; i < maxRetry; i++) {
            if (isValidJson(json)) return json;

            json = tryFixJson(json);
            if (isValidJson(json)) return json;

            json = callAIFix(json);
        }

        throw new RuntimeException("Invalid JSON after retries");
    }

    public String tryFixJson(String json) {
        if (json == null) return null;

        json = json.trim();

        json = json.replaceAll("```json", "");
        json = json.replaceAll("```", "");

        json = json.replaceAll(",\\s*}", "}");
        json = json.replaceAll(",\\s*]", "]");

        long openBrace = json.chars().filter(c -> c == '{').count();
        long closeBrace = json.chars().filter(c -> c == '}').count();
        while (closeBrace < openBrace) {
            json += "}";
            closeBrace++;
        }

        long openBracket = json.chars().filter(c -> c == '[').count();
        long closeBracket = json.chars().filter(c -> c == ']').count();
        while (closeBracket < openBracket) {
            json += "]";
            closeBracket++;
        }

        return json;
    }

    public boolean isValidJson(String json) {
        if (json == null || json.isBlank()) return false;

        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    public String callAIFix(String brokenJson) {
        String fixPrompt = """
                The following JSON is invalid.
                Fix the JSON and return ONLY valid JSON.
                Do not explain anything.
                
                Invalid JSON:
                %s
                """.formatted(brokenJson);

        GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(0.0F)
                .topP(0.1F)
                .responseMimeType("application/json")
                .build();

        GenerateContentResponse response =
                client.models.generateContent(MODEL_NAME, fixPrompt, config);

        return response.text().trim();
    }

}
