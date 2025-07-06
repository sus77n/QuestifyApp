package com.example.questifyapp.service;

import com.example.questifyapp.dto.SubmissionDTO;
import com.example.questifyapp.entity.Exercise;
import com.example.questifyapp.entity.Option;
import com.example.questifyapp.entity.Submission;
import com.example.questifyapp.entity.User;
import com.example.questifyapp.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private OptionService optionService;
    @Autowired
    private AuthService authService;
    @Autowired
    private ExerciseService exerciseService;
}
