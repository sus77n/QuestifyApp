package com.example.questifyapp.repository;

import com.example.questifyapp.entity.Chapter;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    @Override
    <S extends Chapter> List<S> findAll(Example<S> example);

    @Override
    Optional<Chapter> findById(Long id);
}
