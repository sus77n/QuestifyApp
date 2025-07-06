package com.example.questifyapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChapterService {
    @Autowired
    private ChapterRepository chapterRepository;

    public List<Chapter> getAllChapters() {
        return chapterRepository.findAll();
    }

    public void addChapter(Chapter chapter) {
        chapterRepository.save(chapter);
    }

    public void deleteChapter(Chapter chapter) {
        chapterRepository.delete(chapter);
    }

    public void updateChapter(Chapter chapter) {
        chapterRepository.save(chapter);
    }

    public Long totalChapters() {
        return chapterRepository.count();
    }

    public Chapter getChapterById(long id) {
        return chapterRepository.findById(id).orElse(null);
    }

    public List<Lesson> getLessonsByChapterId(Long id) {
        Chapter chapter = getChapterById(id);
        return chapter.getLessons();
    }
}
