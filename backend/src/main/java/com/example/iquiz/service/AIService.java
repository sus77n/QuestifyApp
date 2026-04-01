package com.example.iquiz.service;

import com.example.iquiz.dto.ai.*;
import com.example.iquiz.dto.learningUnit.CreateExerciseCategoryDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.enums.AITaskType;
import com.example.iquiz.enums.PromptTemplate;
import com.example.iquiz.exception.ConflictException;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.mapper.AIContentMapper;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.LearningUnitRepository;
import com.example.iquiz.service.learningUnit.LearningUnitService;
import com.example.iquiz.utility.AIUtil;
import com.example.iquiz.utility.MarkdownUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class AIService {
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private LearningUnitRepository learningUnitRepository;
    @Autowired
    private MarkdownUtil markdownUtil;
    @Autowired
    private AIUtil aIUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AIContentMapper aIContentMapper;
    @Autowired
    @Qualifier("aiExecutor")
    private Executor aiExecutor;

    private final Semaphore aiLimiter = new Semaphore(1);

    @Transactional
    public List<CreateExerciseCategoryDto> defineExerciseCategory(UUID originalExCateId) {
        LearningUnit exCategory = learningUnitRepository.findById(originalExCateId)
                .orElseThrow(() -> new ResourceNotFoundException("LearningUnit (Exercise Category)", "Id", originalExCateId));

        LearningUnit lesson = exCategory.getParent();

        List<Exercise> exercises = exerciseRepository.findByParent_Id(originalExCateId);
        if (exercises.isEmpty()) {
            throw new ResourceNotFoundException("Exercises in Exercise Category", "Id", originalExCateId);
        }
        List<ExerciseCompactDto> compactExercises = exercises.stream().map(aIContentMapper::toExerciseCompactDto).toList();
        String exercisesBlock = markdownUtil.exercisesToCompactText(compactExercises);
        String template = markdownUtil.loadPrompt(PromptTemplate.DEFINE_EXERCISE_CATEGORY);
        String prompt = template.formatted(
                lesson.getId(),
                lesson.getName(),
                exCategory.getId(),
                exCategory.getName(),
                exercisesBlock
        );

        String responseJson = aIUtil.sendPrompt(prompt, AITaskType.DEFINE_EXERCISE_CATEGORY);

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<CreateExerciseCategoryDto> dtos = mapper.readValue(
                    responseJson,
                    new TypeReference<>() {
                    }
            );
            return dtos;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ConflictException("Failed to parse AI response: " + ex.getMessage());
        }
    }

    public List<ExportedCategoryDto> generateExercises(GenerateExercisesRequest request) {

        UUID lessonId = request.lessonId();

        List<CreateExerciseCategoryDto> categories = request.categories();
        List<LearningUnit> categoryEntities =
                aIUtil.buildGeneratedCategories(lessonId, categories);

        String template =
                markdownUtil.loadPrompt(PromptTemplate.CONTEXT_HEADER) + "\n\n" +
                markdownUtil.loadPrompt(PromptTemplate.GENERATE_EXERCISES);

        ExerciseGenerationConfig cfg = ExerciseGenerationConfig.builder().build();
        String config = markdownUtil.loadPrompt(PromptTemplate.EXERCISE_GENERATION_CONFIG)
                .formatted(
                        cfg.getMultipleChoice(),
                        cfg.getSelectMultiple(),
                        cfg.getTrueFalse(),
                        cfg.getMatching(),
                        cfg.getReordering(),
                        cfg.getFillInBlank(),
                        cfg.getShortAnswer(),
                        cfg.getTotal()
                );

        template = template + "\n\n" + config;

        List<CategoryCompactDto> categoryDtos = categoryEntities.stream()
                .map(aIContentMapper::toCategoryCompactDto)
                .toList();

        List<CompletableFuture<List<ExportedCategoryDto>>> futures = new ArrayList<>();
        for (CategoryCompactDto category : categoryDtos) {
            String finalTemplate = template;
            CompletableFuture<List<ExportedCategoryDto>> future =
                    CompletableFuture.supplyAsync(() -> {

                        String categoryBlock =
                                markdownUtil.categoryWithExercisesToCompactText(category, 1);

                        String prompt = finalTemplate.formatted(categoryBlock);

                        try {
                            aiLimiter.acquire();
                            String response = aIUtil.sendPrompt(prompt, AITaskType.QUESTION_GENERATION);
                            response = aIUtil.validationAIResponse(response);

                            return objectMapper.readValue(
                                    response,
                                    new TypeReference<List<ExportedCategoryDto>>() {
                                    }
                            );

                        } catch (Exception ex) {
                            throw new CompletionException(
                                    new ConflictException("AI processing failed: " + ex.getMessage())
                            );
                        } finally {
                            aiLimiter.release();
                        }

                    }, aiExecutor).orTimeout(10, TimeUnit.MINUTES);

            futures.add(future);
        }

        CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).join();

        aIUtil.saveAllGeneratedCategories(categoryEntities);

        return futures.stream()
                .flatMap(f -> f.join().stream())
                .toList();

    }


    @Transactional
    public List<ExportedCategoryDto> generateCombinedExercises(UUID lessonId, List<CreateExerciseCategoryDto> categories) {
//        List<LearningUnit> categoryEntities = learningUnitService.saveGeneratedCategoriesBulk(lessonId, categories);
//
//        StringBuilder exerciseCategoryContainingExercises = new StringBuilder();
//        for (int i = 0; i < categoryEntities.size(); i++) {
//            String exercisesBlock = markdownUtil.categoryWithExercisesToCompactText(categoryEntities.get(i), i + 1);
//            exerciseCategoryContainingExercises.append(exercisesBlock);
//        }
//
//        String template = markdownUtil.loadPrompt(PromptTemplate.GENERATE_EXERCISES);
//        String prompt = template.formatted(
//                exerciseCategoryContainingExercises.toString()
//        );
//
//        String response = aIUtil.sendPrompt(prompt, AITaskType.QUESTION_GENERATION);
//
//        try {
//            List<ExportedCategoryDto> dtos = objectMapper.readValue(
//                    response,
//                    new TypeReference<>() {
//                    }
//            );
//
//            return dtos;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw new ConflictException("Failed to parse AI response: " + ex.getMessage());
//        }
        return null;
    }

}
