package com.example.iquiz.service;

import com.example.iquiz.entity.UserMastery;
import com.example.iquiz.entity.UserMasteryId;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.repository.UserMasteryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserMasteryService {

    private final UserMasteryRepository repo;

    public UserMastery save(UserMastery mastery) {
        return repo.save(mastery);
    }

    public UserMastery findById(UserMasteryId id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("UserMastery", "id", id));
    }

    public List<UserMastery> findByUser(UUID userId) {
        return repo.findByUserId(userId);
    }

    public void delete(UserMasteryId id) {
        repo.deleteById(id);
    }

    public UserMastery updateMastery(UUID userId, UUID lessonId, UUID exerciseTypeId, boolean isCorrect) {
        UserMasteryId id = new UserMasteryId(userId, lessonId, exerciseTypeId);
        UserMastery mastery = repo.findById(id).orElseGet(() -> {
            UserMastery m = new UserMastery();
            m.setId(id);
            m.setAccuracy(0.0);
            m.setWrongCount(0);
            m.setCorrectCount(0);
            return m;
        });

        mastery.setWrongCount(mastery.getWrongCount() + 1);
        if (isCorrect) {
            mastery.setCorrectCount(mastery.getCorrectCount() + 1);
        }
        mastery.setAccuracy((double) mastery.getCorrectCount() / (mastery.getCorrectCount() + mastery.getWrongCount()) * 100.0);

        return repo.save(mastery);
    }
}
