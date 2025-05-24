package com.example.questifyapp.service;

import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.entity.Lesson;
import com.example.questifyapp.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {
    @Autowired
    private LessonRepository lessonRepository;

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public Lesson getLessonById(long id) {
        return lessonRepository.findById(id).get();
    }

    public List<Exercise> getExercisesByLessonId(Long id) {
        Lesson lesson = getLessonById(id);
        return lesson.getExercises();
    }
}
