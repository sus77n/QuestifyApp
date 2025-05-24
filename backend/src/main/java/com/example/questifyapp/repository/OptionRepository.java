package com.example.questifyapp.repository;

import com.example.questifyapp.entity.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findByExerciseId(Integer exerciseId);
}
