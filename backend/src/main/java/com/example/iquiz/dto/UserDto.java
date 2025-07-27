package com.example.iquiz.dto;

import com.example.iquiz.enums.UserRole;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        UserRole role,
        LocalDateTime createdAt
) {
}
