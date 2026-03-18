package com.example.iquiz.mapper;

import com.example.iquiz.dto.LessonConfigDto;
import com.example.iquiz.entity.LessonConfig;
import org.springframework.stereotype.Component;

@Component
public class LessonConfigMapper {

    public LessonConfig toEntity(LessonConfigDto dto) {
        LessonConfig e = new LessonConfig();
        e.setId(dto.id());
        e.setQuestionsPerAttempt(dto.questionsPerAttempt());
        e.setPassThreshold(dto.passThreshold());
        e.setNoRepeatScope(dto.noRepeatScope());
        return e;
    }

    public LessonConfigDto toDto(LessonConfig e) {
        return new LessonConfigDto(
                e.getId(),
                e.getLesson().getId(),
                e.getQuestionsPerAttempt(),
                e.getPassThreshold(),
                e.isNoRepeatScope()
        );
    }
}
