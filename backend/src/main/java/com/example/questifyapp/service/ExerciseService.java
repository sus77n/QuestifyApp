package com.example.questifyapp.service;

import com.example.questifyapp.entity.Course;
import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.entity.Option;
import com.example.questifyapp.repository.ExerciseRepository;
import com.example.questifyapp.repository.OptionRepository;
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

    public Exercise getExerciseById(Long exerciseId) {
        return exerciseRepository.findById(exerciseId).orElse(null);
    }

    public List<Option> getOptionsByExerciseId(Long exerciseId) {
        Exercise exercise = getExerciseById(exerciseId);
        return exercise.getOptions();
    }


}
