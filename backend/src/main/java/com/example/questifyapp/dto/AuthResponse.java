package com.example.questifyapp.dto;

import com.example.questifyapp.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(
        String token,
        Instant issuedAt,
        Instant expiresAt,
        Long id,
        String username,
        String email,
        UserRole role
) {

}