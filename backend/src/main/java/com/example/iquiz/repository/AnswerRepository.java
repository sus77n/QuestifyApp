package com.example.iquiz.repository;

import com.example.iquiz.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {

}
