package com.example.iquiz.service;

import com.example.iquiz.dto.LessonConfigDto;
import com.example.iquiz.entity.LessonConfig;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.mapper.LessonConfigMapper;
import com.example.iquiz.repository.LessonConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonConfigService {
    private final LessonConfigRepository repo;
    private final LessonConfigMapper mapper;

    public LessonConfigDto findByLessonId(UUID lessonId) {
        return mapper.toDto(repo.findByLessonId(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("LessonConfig not found", "LessonConfig", lessonId)));
    }

    public LessonConfigDto update(UUID lessonId, LessonConfigDto dto) {
        LessonConfig entity = repo.findByLessonId(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("LessonConfig not found", "LessonConfig", lessonId));

        entity.setNoRepeatScope(dto.noRepeatScope());
        entity.setPassThreshold(dto.passThreshold());
        entity.setQuestionsPerAttempt(dto.questionsPerAttempt());

        return mapper.toDto(repo.save(entity));
    }

    public void delete(UUID lessonId) {
        repo.deleteByLessonId(lessonId);
    }

}
