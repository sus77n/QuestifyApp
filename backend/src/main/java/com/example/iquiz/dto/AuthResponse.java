package com.example.iquiz.dto;

import com.example.iquiz.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String token,
        Instant tokenExpiration,
        LocalDateTime createdAt,
        UserRole role
) {

}