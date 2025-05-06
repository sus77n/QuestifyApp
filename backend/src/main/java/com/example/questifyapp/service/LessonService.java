package com.example.questifyapp.service;

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

    public List<Lesson> getLessonsByChapterId(Long chapterId) {
        return getAllLessons().stream().filter(lesson -> {
            return lesson.getChapter().getId() == chapterId;
        }).toList();
    }
}
