package com.example.questifyapp.mapper;

import com.example.questifyapp.dto.option.OptionRequestDto;
import com.example.questifyapp.dto.option.OptionResponseDto;
import com.example.questifyapp.entity.Option;

public class OptionMapper {
    public static OptionResponseDto toDto(Option option) {
        return new OptionResponseDto(
                option.getId(),
                option.getText()
        );
    }

    public static Option toEntity(OptionRequestDto dto) {
        return new Option(
                dto.id(),
                dto.text(),
                dto.isCorrect(),
                null
        );
    }
}
