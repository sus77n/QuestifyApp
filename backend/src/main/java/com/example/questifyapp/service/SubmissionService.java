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


}
