package com.example.iquiz.service;

import com.example.iquiz.dto.lesssonConfig.LessonConfigDto;
import com.example.iquiz.entity.LessonConfig;
import com.example.iquiz.repository.LessonConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LessonConfigService {
    private final LessonConfigRepository repo;

    public LessonConfigDto save(LessonConfigDto dto) {
        LessonConfig entity = new LessonConfig();
        entity.setLessonId(dto.lessonId());
        entity.setQuestionsPerAttempt(dto.questionsPerAttempt());
        entity.setNoRepeatScope(dto.noRepeatScope());
        return toDto(repo.save(entity));
    }

    public Optional<LessonConfigDto> findByLessonId(Long lessonId) {
        return repo.findByLessonId(lessonId).map(this::toDto);
    }

    public LessonConfigDto update(Long lessonId, LessonConfigDto dto) {
        LessonConfig entity = repo.findByLessonId(lessonId)
                .orElseThrow(() -> new RuntimeException("LessonConfig not found"));
        entity.setQuestionsPerAttempt(dto.questionsPerAttempt());
        entity.setNoRepeatScope(dto.noRepeatScope());
        return toDto(repo.save(entity));
    }

    public void delete(Long lessonId) {
        repo.deleteByLessonId(lessonId);
    }

    private LessonConfigDto toDto(LessonConfig e) {
        return new LessonConfigDto(e.getLessonId(), e.getQuestionsPerAttempt(), e.isNoRepeatScope());
    }
}
