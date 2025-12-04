package com.example.iquiz.service;

import com.example.iquiz.entity.ExerciseCategoryDistribution;
import com.example.iquiz.entity.ExerciseCategoryDistributionId;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.repository.ExerciseCategoryDistributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonTypeDistributionService {

    private final ExerciseCategoryDistributionRepository repo;

    public ExerciseCategoryDistribution save(ExerciseCategoryDistribution dist) {
        return repo.save(dist);
    }

    public ExerciseCategoryDistribution findById(ExerciseCategoryDistributionId id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LessonTypeDistribution not found", "LessonTypeDistribution", id));
    }

    public List<ExerciseCategoryDistribution> findByLesson(UUID lessonId) {
        return repo.findByParentLessonId(lessonId);
    }

    public void delete(ExerciseCategoryDistributionId id) {
        repo.deleteById(id);
    }
}
