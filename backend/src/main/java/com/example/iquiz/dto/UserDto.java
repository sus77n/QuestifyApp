package com.example.iquiz.dto;

import com.example.iquiz.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        UserRole role,
        LocalDateTime createdAt
) {
}
