package com.example.iquiz.repository;

import com.example.iquiz.entity.Exercise;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    @Override
    Optional<Exercise> findById(Long id);

    @Override
    <S extends Exercise> List<S> findAll(Example<S> example);


}
