package com.example.questifyapp.mapper;

import com.example.questifyapp.dto.OptionDTO;
import com.example.questifyapp.entity.Option;

public class OptionMapper {
    public static OptionDTO toDto(Option option) {
        return new OptionDTO(
                option.getId(),
                option.getText(),
                ExerciseMapper.toDto(option.getExercise())
        );
    }

    public static Option toEntity(OptionDTO optionDTO) {
        return new Option(
                optionDTO.id(),
                optionDTO.text(),
                false,
                ExerciseMapper.toEntity(optionDTO.exercise())
        );
    }
}
