package com.example.iquiz.service;

import com.example.iquiz.dto.exercise.ExerciseResponseDto;
import com.example.iquiz.dto.exercise.ExerciseWithAnswerDto;
import com.example.iquiz.dto.learningUnit.CreateExerciseCategoryDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.enums.PromptTemplate;
import com.example.iquiz.exception.ConflictException;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.LearningUnitRepository;
import com.example.iquiz.service.learningUnit.LearningUnitService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private LearningUnitRepository learningUnitRepository;
    @Autowired
    private MarkdownService markdownService;
    @Autowired
    private LearningUnitService learningUnitService;

    public String sendPrompt(String prompt) {
        String contextHeader = markdownService.loadPrompt(PromptTemplate.CONTEXT_HEADER);
        String body = """
                %s
                %s
                """.formatted(
                contextHeader, prompt);

        GenerateContentResponse response =
                client.models.generateContent(MODEL_NAME, body, null);

        return response.text().trim();
//        return body.trim();
    }

    @Transactional
    public List<CreateExerciseCategoryDto> defineExerciseCategory(UUID originalExCateId) {
        LearningUnit exCategory = learningUnitRepository.findById(originalExCateId)
                .orElseThrow(() -> new ResourceNotFoundException("LearningUnit (Exercise Category)", "Id", originalExCateId));

        LearningUnit lesson = exCategory.getParent();
        if (lesson == null) {
            throw new IllegalStateException("Exercise category has no parent lesson");
        }

        List<Exercise> exercises = exerciseRepository.findByParent_Id(originalExCateId);
        if (exercises.isEmpty()) {
            throw new IllegalStateException("No exercises found for exercise category " + originalExCateId);
        }


        StringBuilder exercisesBlock = new StringBuilder();
        int index = 1;
        for (Exercise ex : exercises) {
            exercisesBlock.append("### Exercise ")
                    .append(index++)
                    .append("\n\n");

            exercisesBlock.append("- ID: `").append(ex.getId()).append("`\n");
            exercisesBlock.append("- Type: `").append(ex.getType()).append("`\n");
            exercisesBlock.append("- Difficulty: `").append(ex.getDifficulty()).append("`\n\n");
            exercisesBlock.append("**Question:**\n\n");
            exercisesBlock.append(ex.getQuestion()).append("\n\n");

            if (ex.getCorrectAnswerJson() != null && !ex.getCorrectAnswerJson().isBlank()) {
                exercisesBlock.append("**CorrectAnswersJson (raw):**\n\n");
                exercisesBlock.append("```json\n")
                        .append(ex.getCorrectAnswerJson())
                        .append("\n```\n\n");
            }
        }

        String template = markdownService.loadPrompt(PromptTemplate.DEFINE_EXERCISE_CATEGORY);

        String prompt = template.formatted(
                lesson.getId(),
                lesson.getName(),
                exCategory.getId(),
                exCategory.getName(),
                exercisesBlock
        );


        String responseJson = sendPrompt(prompt);


        try {
            ObjectMapper mapper = new ObjectMapper();
            List<CreateExerciseCategoryDto> dtos = mapper.readValue(
                    responseJson,
                    new TypeReference<List<CreateExerciseCategoryDto>>() {
                    }
            );

            return dtos;
        } catch (Exception ex) {
            throw new ConflictException("Failed to parse AI response");
        }
    }


    @Transactional
    public List<Exercise> generateExercises(UUID lessonId ,List<CreateExerciseCategoryDto> categories) {
        List<LearningUnit> categoryEntities = learningUnitService.saveGeneratedCategoriesBulk(lessonId, categories);



        return null;
    }
}
