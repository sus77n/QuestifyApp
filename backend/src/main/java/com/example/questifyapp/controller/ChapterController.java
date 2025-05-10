package com.example.questifyapp.controller;

import com.example.questifyapp.entity.Chapter;
import com.example.questifyapp.repository.ChapterRepository;
import com.example.questifyapp.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chapter")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @GetMapping("/{courseId}")
    public ResponseEntity<List<Chapter>> getAllChapters(@PathVariable Long courseId) {
        List<Chapter> chapters = chapterService.getChaptersByCourseId(courseId);
        return ResponseEntity.ok(chapters);
    }

}
