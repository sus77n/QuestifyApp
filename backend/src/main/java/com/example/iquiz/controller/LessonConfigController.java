package com.example.iquiz.controller;

import com.example.iquiz.dto.lesssonConfig.LessonConfigDto;
import com.example.iquiz.entity.LessonConfig;
import com.example.iquiz.service.LessonConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/lesson-configs")
@RequiredArgsConstructor
public class LessonConfigController {

    private final LessonConfigService service;

    @PostMapping
    public ResponseEntity<LessonConfigDto> create(@RequestBody LessonConfigDto dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonConfigDto> getByLesson(@PathVariable Long lessonId) {
        return service.findByLessonId(lessonId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<LessonConfigDto> update(@PathVariable Long lessonId,
                                                  @RequestBody LessonConfigDto dto) {
        return ResponseEntity.ok(service.update(lessonId, dto));
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> delete(@PathVariable Long lessonId) {
        service.delete(lessonId);
        return ResponseEntity.noContent().build();
    }
}

