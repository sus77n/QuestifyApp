package com.example.iquiz.mapper;

import com.example.iquiz.dto.attempt.AttemptDto;
import com.example.iquiz.dto.attempt.AttemptResponseDto;
import com.example.iquiz.dto.attempt.AttemptWithDetailsDto;
import com.example.iquiz.entity.Attempt;
import com.example.iquiz.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = AttemptDetailMapper.class)
public interface AttemptMapper {

    @Mapping(target = "attemptId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "lessonId", source = "lesson.id")
    @Mapping(target = "status", source = "attemptStatus")
    @Mapping(target = "results", ignore = true)
    @Mapping(target = "feedback", ignore = true)
    AttemptResponseDto toResponseDto(Attempt attempt);

    @Mapping(target = "attemptId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "lessonId", source = "lesson.id")
    @Mapping(target = "status", source = "attemptStatus")

    @Mapping(target = "username", source = "user", qualifiedByName = "resolveUsername")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "lessonName", source = "lesson.name")
    AttemptDto toDto(Attempt attempt);

    @Mapping(target = "attemptId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "lessonId", source = "lesson.id")
    @Mapping(target = "status", source = "attemptStatus")
    @Mapping(target = "username", source = "user", qualifiedByName = "resolveUsername")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "lessonName", source = "lesson.name")
    AttemptWithDetailsDto toWithDetailsDto(Attempt attempt);

    @Named("resolveUsername")
    default String resolveUsername(User user) {
        if (user == null) return null;

        if (user.getFirstName() != null && user.getLastName() != null) {
            return user.getFirstName() + " " + user.getLastName();
        }

        return user.getUsername();
    }
}