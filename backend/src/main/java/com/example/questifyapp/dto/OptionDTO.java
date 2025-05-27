package com.example.questifyapp.dto;

import com.example.questifyapp.entity.Option;

public record OptionDTO(
        Long id,
        String content
)
{
    public static OptionDTO fromEntity(Option option) {
        return new OptionDTO(
                option.getId(),
                option.getContent()
        );
    }
}