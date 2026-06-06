package com.example.iquiz.utility;

import com.example.iquiz.annotation.LogAI;
import com.example.iquiz.dto.learningUnit.CreateExerciseCategoryDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.entity.LearningUnitType;
import com.example.iquiz.entity.User;
import com.example.iquiz.enums.AITaskType;
import com.example.iquiz.enums.PromptTemplate;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.mapper.LearningUnitMapper;
import com.example.iquiz.repository.ExerciseCategoryRepository;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.LearningUnitRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private LearningUnitRepository learningUnitRepository;
    @Autowired
    private ExerciseCategoryRepository exerciseCategoryRepository;
    @Autowired
    private LearningUnitMapper learningUnitMapper;
    @Autowired
    private ExerciseRepository exerciseRepository;

    @LogAI
    public String sendPrompt(String prompt, AITaskType taskType) {

        List<String> models = List.of(
                getModelByTask(taskType)
        );

        for (String model : models) {
            try {
                return generate(prompt, model);
            } catch (Exception e) {
                System.out.println("Model failed: " + model);
                e.printStackTrace();
            }
        }

        throw new RuntimeException("All AI models failed");
    }

    private String generate(String prompt, String modelName) {
        String contextHeader = markdownUtil.loadPrompt(PromptTemplate.CONTEXT_HEADER);

        String body = """
                %s
                
                User Request:
                %s
                """.formatted(contextHeader, prompt);

        GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(0.1F)
                .topP(0.8F)
                .responseMimeType("application/json")
                .build();

        GenerateContentResponse response =
                client.models.generateContent(modelName, body, config);

        return response.text().trim();
    }

    public String getModelByTask(AITaskType taskType) {
        return switch (taskType) {
//            case DEFINE_EXERCISE_CATEGORY -> "gemini-2.5-flash";
//            case QUESTION_GENERATION -> "gemini-2.0-flash";
//            case FEEDBACK_ANALYSIS -> "gemini-2.0-flash";
            default -> "gemini-2.5-flash";
        };
    }

    public String validationAIResponse(String json) {
        int maxRetry = 3;

        for (int i = 0; i < maxRetry; i++) {
            if (isValidJson(json)) return json;

            json = tryFixJson(json);
            if (isValidJson(json)) return json;

//            json = callAIFix(json);
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

//    public String callAIFix(String brokenJson) {
//        String fixPrompt = """
//                The following JSON is invalid.
//                Fix the JSON and return ONLY valid JSON.
//                Do not explain anything.
//
//                Invalid JSON:
//                %s
//                """.formatted(brokenJson);
//
//        GenerateContentConfig config = GenerateContentConfig.builder()
//                .temperature(0.0F)
//                .topP(0.1F)
//                .responseMimeType("application/json")
//                .build();
//
//        GenerateContentResponse response =
//                client.models.generateContent(MODEL_NAME, fixPrompt, config);
//
//        return response.text().trim();
//    }

    public List<LearningUnit> buildGeneratedCategories(UUID parentId, List<CreateExerciseCategoryDto> dtos) {
        User user = userUtil.getUserFromAuthContext();

        LearningUnit lesson = learningUnitRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Learning Unit", "id", parentId));

        List<LearningUnit> units = new ArrayList<>();

        for (CreateExerciseCategoryDto dto : dtos) {

            LearningUnitType type = exerciseCategoryRepository.findByName(dto.type())
                    .orElseThrow(() -> new ResourceNotFoundException("Learning Unit Type", "name", dto.type()));

            LearningUnit newCate = learningUnitMapper.generatedCategoryToEntity(dto);
            newCate.setParent(lesson);
            newCate.setType(type);
            newCate.setCreatedBy(user);

            List<Exercise> exercises = exerciseRepository.findAllById(dto.exerciseIds());
            exercises.forEach(ex -> ex.setParent(newCate));

            newCate.setExercises(exercises);

            units.add(newCate);
        }

        return units;
    }

    @Transactional
    public List<LearningUnit> saveAllGeneratedCategories(List<LearningUnit> categories) {

        List<LearningUnit> savedCategories = learningUnitRepository.saveAll(categories);

        List<Exercise> allExercises = savedCategories.stream()
                .flatMap(c -> c.getExercises().stream())
                .toList();

        exerciseRepository.saveAll(allExercises);

        return savedCategories;
    }
}
