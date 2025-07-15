package com.example.questifyapp.service;

import com.example.questifyapp.dto.SubmissionDto;
import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.entity.Option;
import com.example.questifyapp.entity.Submission;
import com.example.questifyapp.entity.User;
import com.example.questifyapp.mapper.SubmissionMapper;
import com.example.questifyapp.repository.ExerciseRepository;
import com.example.questifyapp.repository.OptionRepository;
import com.example.questifyapp.repository.SubmissionRepository;
import com.example.questifyapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private OptionRepository optionRepository;

    public SubmissionDto submit(SubmissionDto submissionDTO) {

        Exercise exercise = exerciseRepository.findById(submissionDTO.exerciseId()).orElseThrow(() -> new NullPointerException("exercise not found"));
        User user = userRepository.findById(submissionDTO.userId()).orElseThrow(() -> new NullPointerException("User not found"));

        Submission submission = SubmissionMapper.toEntity(submissionDTO);
        submission.setExercise(exercise);
        submission.setStudent(user);

        if (submissionDTO.selectedOptionId() != 0) {
            Option option = optionRepository.findById(submissionDTO.selectedOptionId()).orElse(null);
            submission.setSelectedOption(option);
            if (option.isCorrect()) {
                submission.setScore(BigDecimal.valueOf(100));
            } else {
                submission.setScore(BigDecimal.valueOf(0));
            }
        } else  {
            if (exercise.getAnswer().contains(submissionDTO.answer())
                    && (exercise.getAnswer().length()/2 <= submissionDTO.answer().length())) {
                submission.setScore(BigDecimal.valueOf(100));
            } else  {
                submission.setScore(BigDecimal.valueOf(0));
            }
        }

        submissionRepository.save(submission);
        return SubmissionMapper.toDto(submission);
    }

//    public List<SubmissionDTO> getSubmissionsByCourseIdAndUserId(Long courseId, Long userId) {
//        List<Submission> submissions = submissionRepository.findByCourseIdAndUserId(courseId, userId);
//        return submissions.stream().map(SubmissionMapper::toDto).toList();
//    }
}
