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
