package com.example.questifyapp.service;

import com.example.questifyapp.entity.Chapter;
import com.example.questifyapp.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChapterService {
    @Autowired
    private ChapterRepository chapterRepository;

    public List<Chapter> getAllChapters() {
        return chapterRepository.findAll();
    }

    public Chapter getChapterById(long id) {
        return chapterRepository.findById(id).orElse(null);
    }

    public List<Chapter> getChaptersByCourseId(Long courseId) {
        return getAllChapters().stream().filter(chapter -> {
            return chapter.getCourse().getId() == courseId;
        }).toList();
    }
}
