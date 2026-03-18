package com.example.iquiz.service;

import com.example.iquiz.entity.AILog;
import com.example.iquiz.entity.User;
import com.example.iquiz.enums.AITaskStatus;
import com.example.iquiz.enums.AITaskType;
import com.example.iquiz.repository.AILogRepository;
import com.example.iquiz.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AILogService {

    @Autowired
    private AILogRepository aiLogRepository;
    @Autowired
    private UserRepository userRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLogAsync(AITaskType taskType, String input, String output,
                             AITaskStatus status, LocalDateTime timestamp,
                             String modelName, String username) {
        try {
            User actor = userRepository.findByUsername(username).orElse(null);

            if (actor == null) {
                log.warn("User with username '{}' not found. Falling back to SYSTEM.", username);
                actor = userRepository.findByUsername("system").orElse(null);
            }

            if (actor == null) {
                log.error("CRITICAL: Even 'system' user is missing from DB. Log cannot be saved.");
                return;
            }

            AILog logEntry = AILog.builder()
                    .taskType(taskType)
                    .actor(actor)
                    .requestTimestamp(timestamp)
                    .inputPayload(input)
                    .outputResponse(output)
                    .modelAI(modelName)
                    .status(status)
                    .build();

            aiLogRepository.save(logEntry);

        } catch (Exception e) {
            log.error("Failed to write AI Log asynchronously", e);
        }
    }
}