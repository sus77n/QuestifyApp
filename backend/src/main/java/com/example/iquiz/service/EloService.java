package com.example.iquiz.service;

import com.example.iquiz.entity.AttemptDetail;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EloService {

    private final EloUpdater eloUpdater;

    // Configurable parameters — you may want to move to application.properties
    private final int MAX_RETRIES = 3;

    public void updateRatingsForAttempt(UUID userId, List<AttemptDetail> details) {
        for (AttemptDetail d : details) {
            updateWithRetry(userId, d);
        }
    }

    private void updateWithRetry(UUID userId, AttemptDetail d) {
        int attempts = 0;
        while (true) {
            try {
                eloUpdater.performUpdate(userId, d);
                return;
            } catch (OptimisticLockingFailureException | OptimisticLockException ex) {
                attempts++;
                if (attempts > MAX_RETRIES) throw ex;
                try {
                    Thread.sleep(50L * attempts);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}

