package com.example.questifyapp.dto;

import com.example.questifyapp.entity.Chapter;

import java.util.List;

public record ChapterDTO(
        Long id,
        String title,
        List<LessonDTO> lessons
) {
    public static ChapterDTO fromEntity(Chapter chapter) {
        return new ChapterDTO(
                chapter.getId(),
                chapter.getTitle(),
                chapter.getLessons().stream().map(LessonDTO::fromEntity).toList()
        );
    }
}
