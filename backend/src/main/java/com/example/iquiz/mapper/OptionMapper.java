package com.example.iquiz.mapper;

import com.example.iquiz.dto.option.OptionRequestDto;
import com.example.iquiz.dto.option.OptionResponseDto;
import com.example.iquiz.entity.Option;
import com.example.iquiz.repository.ExerciseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OptionMapper {

    @Autowired
    private ExerciseRepository exerciseRepository;

    public OptionResponseDto toDto(Option option) {
        return new OptionResponseDto(
                option.getId(),
                option.getText()
        );
    }

    public Option toEntity(OptionRequestDto dto) {
        return new Option(
                dto.id(),
                dto.text(),
                dto.isCorrect(),
                dto.explanation(),
                exerciseRepository.findById(dto.exerciseId())
                        .orElseThrow(() -> new EntityNotFoundException("Exercise with id: " + dto.exerciseId()))
        );
    }

    public List<OptionResponseDto> toDtoList(List<Option> options) {
        return options.stream().map(option -> toDto(option)).collect(Collectors.toList());
    }
}
