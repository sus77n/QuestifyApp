package com.example.questifyapp.service;

import com.example.questifyapp.entity.*;
import com.example.questifyapp.repository.ExerciseRepository;
import com.example.questifyapp.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private CourseService courseService;
    @Autowired
    private ChapterService chapterService;

    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    public List<Submission> getSubmissionsByUserId(long userId) {
        return submissionRepository.findByStudentId(userId);
    }

    public Submission getSubmissionById(long submissionId) {
        return submissionRepository.findById(submissionId).orElse(null);
    }

    public List<Submission> getSubmissionsByExerciseId(int exerciseId) {
        return submissionRepository.findByExerciseId(exerciseId);
    }

    public long countDistinctSubmissionsByCourseId(int courseId) {
        Course course = courseService.getCourseById(courseId);
        long count = 0;

        for (Chapter chapter : course.getChapters()) {
            count += countDistinctSubmissionsByChapterId(chapter.getId());
        }

        return count;
    }

    public long countDistinctSubmissionsByChapterId(long chapterId) {
        Chapter chapter = chapterService.getChapterById(chapterId);
        long count = 0;

        for (Lesson lesson : chapter.getLessons()) {
            count += countDistinctSubmissionsByLessonId(lesson.getId());
        }

        return count;
    }

    public long countDistinctSubmissionsByLessonId(long lessonId) {
        Lesson lesson = lessonService.getLessonById(lessonId);
        long count = 0;
        for (Exercise exercise : lesson.getExercises()) {
            if (submissionRepository.existsByExerciseIdAndScoreBetween50And100(exercise.getId())) {
                count++;
            };
        }
        return count;
    }

}
