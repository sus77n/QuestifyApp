package com.example.questifyapp.service;

import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService {
    @Autowired
    private ExerciseRepository exerciseRepository;

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public List<Exercise> getExercisesByLessonId(Long lessonId) {
        return getAllExercises().stream().filter(exercise -> {
            return exercise.getLesson().getId() == lessonId;
        }).toList();
    }

    public Exercise getExerciseById(Long exerciseId) {
        return exerciseRepository.findById(exerciseId).orElse(null);
    }
}
