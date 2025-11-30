package com.example.iquiz.mapper;

import com.example.iquiz.dto.answer.AnswerRequestDto;
import com.example.iquiz.dto.answer.OptionDto;
import com.example.iquiz.entity.Answer;
import com.example.iquiz.exception.ResourceNotFoundException;
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
    private ExerciseRepository exerciseRepository;
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
            // metadata malformed → ignore safely
            side = null;
        }

        return new OptionDto(
                answer.getId(),
                answer.getText(),
                answer.getHeader(),
                side
        );
    }

    public Answer toEntity(AnswerRequestDto dto) {
        return new Answer(
                dto.id(),
                dto.text(),
                dto.header(),
                dto.metadata(),
                exerciseRepository.findById(dto.exerciseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", dto.exerciseId()))
        );
    }

    public List<OptionDto> toDtoList(List<Answer> answers) {
        return answers.stream().map(option -> toOptionDto(option)).collect(Collectors.toList());
    }
}
