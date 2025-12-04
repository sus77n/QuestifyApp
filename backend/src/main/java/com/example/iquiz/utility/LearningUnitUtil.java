package com.example.iquiz.utility;

import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.entity.LessonConfig;
import com.example.iquiz.repository.LessonConfigRepository;
import com.example.iquiz.repository.AttemptDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class LearningUnitUtil {

    @Autowired
    private AttemptDetailRepository attemptDetailRepository;

    @Autowired
    private LessonConfigRepository lessonConfigRepository;

    public long getNumberOfCompletedExercise(UUID userId, LearningUnit learningUnit) {
        if (learningUnit.getExercises() != null && learningUnit.getExercises().size() > 0) {
            long lessonConfigCount = lessonConfigRepository.findByLessonId(learningUnit.getParent().getId())
                    .map(LessonConfig::getQuestionsPerAttempt)
                    .orElse(0);
            long count = attemptDetailRepository.countPassedExercisesByUserIdAndLearningUnitId(userId, learningUnit.getParent().getId());
            return count >= lessonConfigCount ? lessonConfigCount : count;
        }

        long result = 0;
        for (LearningUnit lu : learningUnit.getChildren()) {
            result += getNumberOfCompletedExercise(userId, lu);
        }

        return result;
    }

    public long countExercises(LearningUnit learningUnit) {
        long numberOfExercises = 0;

        if (learningUnit.getExercises() != null && learningUnit.getExercises().size() > 0) {
            long lessonConfigCount = lessonConfigRepository.findByLessonId(learningUnit.getParent().getId())
                    .map(LessonConfig::getQuestionsPerAttempt)
                    .orElse(0);
            return lessonConfigCount;
        }

        for (LearningUnit lu : learningUnit.getChildren()) {
            numberOfExercises += countExercises(lu);
        }

        return numberOfExercises;
    }

    public List<Exercise> getAllExercises(LearningUnit learningUnit) {
        List<Exercise> exercises = new ArrayList<>();

        if (learningUnit.getExercises() != null && !learningUnit.getExercises().isEmpty()) {
            return learningUnit.getExercises();
        }

        for (LearningUnit child : learningUnit.getChildren()) {
            exercises.addAll(getAllExercises(child));
        }
        return exercises;
    }
}
