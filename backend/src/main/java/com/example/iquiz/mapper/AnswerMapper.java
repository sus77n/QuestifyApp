package com.example.iquiz.mapper;

import com.example.iquiz.dto.answer.OptionDto;
import com.example.iquiz.entity.Answer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AnswerMapper {

    @Autowired
    private ObjectMapper objectMapper;

    public OptionDto toOptionDto(Answer answer) {

        String side = extractSide(answer.getMetadata());

        Map<String, String> metadataMap = Map.of("side", side);

        return new OptionDto(
                answer.getId(),
                answer.getText(),
                answer.getHeader(),
                metadataMap
        );
    }

    private String extractSide(String metadata) {
        if (metadata == null || metadata.isBlank()) {
            return "left";
        }

        metadata = metadata.trim();

        try {
            if ("left".equalsIgnoreCase(metadata) || "right".equalsIgnoreCase(metadata)) {
                return metadata.toLowerCase();
            }

            if (metadata.startsWith("{")) {
                Map<String, String> map = objectMapper.readValue(
                        metadata,
                        new TypeReference<Map<String, String>>() {
                        }
                );

                return map.getOrDefault("side", "left");
            }

        } catch (Exception ignored) {
        }

        return "left";
    }

    public Answer toEntity(OptionDto dto) {
        Answer answer = new Answer();
        answer.setText(dto.text());
        answer.setHeader(dto.header());

        try {
            if (dto.metadata() != null && !dto.metadata().isEmpty()) {
                String metadataJson = objectMapper.writeValueAsString(dto.metadata());
                answer.setMetadata(metadataJson);
            } else {
                answer.setMetadata(null);
            }
        } catch (Exception e) {
            answer.setMetadata(null);
        }

        return answer;
    }

    public List<OptionDto> toDtoList(List<Answer> answers) {
        return answers.stream().map(option -> toOptionDto(option)).collect(Collectors.toList());
    }
}
