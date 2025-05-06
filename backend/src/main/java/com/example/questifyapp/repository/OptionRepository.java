package com.example.questifyapp.repository;

import com.example.questifyapp.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {
}
