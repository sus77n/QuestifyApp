package com.example.iquiz.service;

import com.example.iquiz.dto.option.OptionRequestDto;
import com.example.iquiz.dto.option.OptionResponseDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.Option;
import com.example.iquiz.mapper.OptionMapper;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OptionService {
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private OptionMapper optionMapper;

    public List<OptionResponseDto> getAllOptions () {
        return optionRepository.findAll().stream()
                .map(option -> optionMapper.toDto(option)).collect(Collectors.toList());
    }

    public OptionResponseDto getOptionById(Long id) {
        return optionMapper.toDto(optionRepository.findById(id).orElse(null));
    }

    public OptionResponseDto saveOption(OptionRequestDto dto) {
        Option option = optionMapper.toEntity(dto);

        optionRepository.save(option);
        return optionMapper.toDto(option);
    }

    public OptionResponseDto updateOption(Long id,OptionRequestDto dto) {
        Option option = optionRepository.findById(id).orElseThrow(() -> new NullPointerException("option not found"));

        Exercise exercise = exerciseRepository.findById(dto.exerciseId())
                .orElseThrow(() -> new NullPointerException("exercise not found"));
        option.setExercise(exercise);

        optionRepository.save(option);
        return optionMapper.toDto(option);
    }

    public void deleteOptionById(Long id) {
        optionRepository.deleteById(id);
    }
}
