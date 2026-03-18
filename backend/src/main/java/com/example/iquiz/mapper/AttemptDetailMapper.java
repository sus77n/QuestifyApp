package com.example.iquiz.mapper;

import com.example.iquiz.dto.attemptDetail.AttemptDetailDto;
import com.example.iquiz.entity.AttemptDetail;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface AttemptDetailMapper {

    @Mapping(target = "exerciseId", source = "exercise.id")
    @Mapping(target = "question", source = "exercise.question")
    @Mapping(target = "exerciseType", source = "exercise.type")
    AttemptDetailDto toDto(AttemptDetail attemptDetail);

    @InheritInverseConfiguration
    AttemptDetail toEntity(AttemptDetailDto attemptDetailDto);
}

