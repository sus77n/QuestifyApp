package com.example.iquiz.aspect;

import com.example.iquiz.enums.AITaskStatus;
import com.example.iquiz.enums.AITaskType;
import com.example.iquiz.service.AILogService;
import com.example.iquiz.utility.AIUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AILogAspect {

    @Autowired
    private AILogService aiLogService;
    @Autowired
    private AIUtil aIUtil;

    @Around("@annotation(com.example.iquiz.annotation.LogAI)")
    public Object logAIInteraction(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime timestamp = LocalDateTime.now();
        String username = "system";

        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        String inputPayload = "N/A";
        AITaskType taskType = AITaskType.UNKNOWN;

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof String && inputPayload.equals("N/A")) {
                inputPayload = (String) arg;
            } else if (arg instanceof AITaskType) {
                taskType = (AITaskType) arg;
            }
        }

        AITaskStatus status = AITaskStatus.PENDING;
        String outputResponse = "";
        Object result = null;

        try {
            result = joinPoint.proceed();

            if (result instanceof String) {
                outputResponse = (String) result;
            }
            status = AITaskStatus.COMPLETED;
            return result;

        } catch (Exception e) {
            status = AITaskStatus.FAILED;
            outputResponse = "ERROR: " + e.getMessage();
            throw e;

        } finally {
            aiLogService.saveLogAsync(
                    taskType,
                    inputPayload,
                    outputResponse,
                    status,
                    timestamp,
                    aIUtil.getModelByTask(taskType),
                    username
            );
        }
    }

}