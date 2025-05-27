package com.example.questifyapp.dto;

import com.example.questifyapp.entity.Chapter;

public record ChapterDTO(
        Long id,
        String title
) {
    public static ChapterDTO fromEntity(Chapter chapter) {
        return new ChapterDTO(
                chapter.getId(),
                chapter.getTitle()
        );
    }
}
