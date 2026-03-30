package com.example.iquiz.mapper;

import com.example.iquiz.dto.ProgressDTO;
import com.example.iquiz.entity.ParticipantProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ParticipantProgressMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "lastAttempt.id", target = "attemptId")
    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.name", target = "courseName")
    @Mapping(source = "course.code", target = "courseCode")
    @Mapping(source = "progressPercent", target = "progressPercent", qualifiedByName = "bigDecimalToDouble")
    @Mapping(source = "bestScore", target = "bestScore", qualifiedByName = "bigDecimalToDouble")
    ProgressDTO toDto(ParticipantProgress entity);

    @Named("bigDecimalToDouble")
    static double mapBigDecimalToDouble(java.math.BigDecimal value) {
        return value != null ? value.doubleValue() : 0.0;
    }

}
