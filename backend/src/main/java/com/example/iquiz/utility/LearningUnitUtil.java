package com.example.iquiz.utility;

import com.example.iquiz.entity.LearningUnit;
import com.example.iquiz.entity.LessonConfig;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.repository.LessonConfigRepository;
import com.example.iquiz.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LearningUnitUtil {

    @Autowired
    private SubmissionRepository submissionRepository;
    private int LIMIT_OF_EXERCISE = 5;

    @Autowired
    private LessonConfigRepository lessonConfigRepository;

    public long getNumberOfCompletedExercise(Long userId, LearningUnit learningUnit) {
        if (learningUnit.getExercises() != null && learningUnit.getExercises().size() > 0) {
            long count = submissionRepository.countPassedExercisesByUserIdAndLearningUnitId(userId, learningUnit.getId());
            return count >= LIMIT_OF_EXERCISE ? LIMIT_OF_EXERCISE : count;
        }

        long result = 0;
        for (LearningUnit lu : learningUnit.getChildren()) {
            result += getNumberOfCompletedExercise(userId, lu);
        }

        return result;
    }

    public long countExercises(LearningUnit unit) {
        long numberOfExercises = 0;

        if (unit.getType().getName().equalsIgnoreCase("lesson")) {
            LessonConfig lessonConfig = lessonConfigRepository.findByLessonId(unit.getId())
                    .orElse(null);
            return lessonConfig != null ? lessonConfig.getQuestionsPerAttempt() : 0;
        }

        for (LearningUnit lu : unit.getChildren()) {
            numberOfExercises += countExercises(lu);
        }

        return numberOfExercises;
    }

}
