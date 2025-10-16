package com.example.iquiz.controller;

import com.example.iquiz.entity.LessonTypeDistribution;
import com.example.iquiz.entity.LessonTypeDistributionId;
import com.example.iquiz.service.LessonTypeDistributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lesson-distributions")
@RequiredArgsConstructor
public class LessonTypeDistributionController {

    private final LessonTypeDistributionService service;

    @PostMapping
    public ResponseEntity<LessonTypeDistribution> create(@RequestBody LessonTypeDistribution dist) {
        return ResponseEntity.ok(service.save(dist));
    }

    @GetMapping("/{lessonId}/{exerciseTypeId}")
    public ResponseEntity<LessonTypeDistribution> getById(@PathVariable Long lessonId, @PathVariable Long exerciseTypeId) {
        LessonTypeDistributionId id = new LessonTypeDistributionId(lessonId, exerciseTypeId);
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<LessonTypeDistribution>> getByLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(service.findByLesson(lessonId));
    }

    @DeleteMapping("/{lessonId}/{exerciseTypeId}")
    public ResponseEntity<Void> delete(@PathVariable Long lessonId, @PathVariable Long exerciseTypeId) {
        LessonTypeDistributionId id = new LessonTypeDistributionId(lessonId, exerciseTypeId);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
