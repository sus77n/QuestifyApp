package com.example.iquiz.service;
import com.example.iquiz.dto.option.OptionRequestDto;
import com.example.iquiz.dto.option.OptionResponseDto;
import com.example.iquiz.entity.Exercise;
import com.example.iquiz.entity.Option;
import com.example.iquiz.mapper.OptionMapper;
import com.example.iquiz.repository.ExerciseRepository;
import com.example.iquiz.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionService {

    private final OptionRepository optionRepository;
    private final ExerciseRepository exerciseRepository;
    private final OptionMapper optionMapper;

    public List<OptionResponseDto> getAllOptions() {
        return optionRepository.findAll().stream()
                .map(optionMapper::toDto)
                .toList();
    }

    public OptionResponseDto getOptionById(Long id) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Option not found with id: " + id));
        return optionMapper.toDto(option);
    }

    public List<OptionResponseDto> getOptionsByExerciseId(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NullPointerException("Exercise not found with id: " + exerciseId));
        return exercise.getOptions().stream()
                .map(optionMapper::toDto)
                .toList();
    }

    @Transactional
    public OptionResponseDto saveOption(OptionRequestDto dto) {
        Exercise exercise = exerciseRepository.findById(dto.exerciseId())
                .orElseThrow(() -> new NullPointerException("Exercise not found with id: " + dto.exerciseId()));

        Option option = optionMapper.toEntity(dto);
        option.setExercise(exercise);

        optionRepository.save(option);
        return optionMapper.toDto(option);
    }

    @Transactional
    public OptionResponseDto updateOption(Long id, OptionRequestDto dto) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("Option not found with id: " + id));

        if (dto.exerciseId() != null) {
            Exercise exercise = exerciseRepository.findById(dto.exerciseId())
                    .orElseThrow(() -> new NullPointerException("Exercise not found with id: " + dto.exerciseId()));
            option.setExercise(exercise);
        }

        option.setText(dto.text());
        option.setCorrect(dto.isCorrect());
        option.setExplanation(dto.explanation());

        optionRepository.save(option);
        return optionMapper.toDto(option);
    }

    public void deleteOptionById(Long id) {
        optionRepository.deleteById(id);
    }
}
