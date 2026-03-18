package com.example.iquiz.repository;

import com.example.iquiz.entity.ParticipantProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParticipantProgressRepository extends JpaRepository<ParticipantProgress, UUID> {

    Optional<ParticipantProgress> findByUserIdAndCourseId(UUID userId, UUID courseId);
}
