package com.example.iquiz.service;

import com.example.iquiz.entity.LessonTypeDistribution;
import com.example.iquiz.entity.LessonTypeDistributionId;
import com.example.iquiz.repository.LessonTypeDistributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LessonTypeDistributionService {

    private final LessonTypeDistributionRepository repo;

    public LessonTypeDistribution save(LessonTypeDistribution dist) {
        return repo.save(dist);
    }

    public Optional<LessonTypeDistribution> findById(LessonTypeDistributionId id) {
        return repo.findById(id);
    }

    public List<LessonTypeDistribution> findByLesson(Long lessonId) {
        return repo.findByLessonId(lessonId);
    }

    public List<LessonTypeDistribution> findByExerciseType(Long exerciseTypeId) {
        return repo.findByExerciseTypeId(exerciseTypeId);
    }

    public void delete(LessonTypeDistributionId id) {
        repo.deleteById(id);
    }
}
