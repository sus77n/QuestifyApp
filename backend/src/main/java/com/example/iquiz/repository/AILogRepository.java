package com.example.iquiz.repository;

import com.example.iquiz.entity.AILog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AILogRepository extends JpaRepository<AILog, UUID> {
}
