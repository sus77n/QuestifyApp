package com.example.iquiz.repository;

import com.example.iquiz.entity.UserMastery;
import com.example.iquiz.entity.UserMasteryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserMasteryRepository extends JpaRepository<UserMastery, UserMasteryId> {
    List<UserMastery> findByUserId(UUID userId);
}
