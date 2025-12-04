package com.example.iquiz.mapper;

import com.example.iquiz.dto.answer.OptionDto;
import com.example.iquiz.entity.Answer;
import com.example.iquiz.repository.ExerciseRepository;
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

        String side = null;

        try {
            if (answer.getMetadata() != null) {
                Map<String, String> map = objectMapper.readValue(
                        answer.getMetadata(),
                        new TypeReference<Map<String, String>>() {}
                );
                side = map.get("side");
            }
        } catch (Exception e) {
            side = null;
        }

        return new OptionDto(
                answer.getId(),
                answer.getText(),
                answer.getHeader(),
                side
        );
    }

    public Answer toEntity(OptionDto dto) {
        Answer answer = new Answer();
        answer.setText(dto.text());
        answer.setHeader(dto.header());

        if (dto.side() != null) {
            try {
                Map<String, String> map = Map.of("side", dto.side());
                String metadataJson = objectMapper.writeValueAsString(map);
                answer.setMetadata(metadataJson);
            } catch (Exception e) {
                answer.setMetadata(null);
            }
        } else {
            answer.setMetadata(null);
        }

        return answer;
    }

    public List<OptionDto> toDtoList(List<Answer> answers) {
        return answers.stream().map(option -> toOptionDto(option)).collect(Collectors.toList());
    }
}
