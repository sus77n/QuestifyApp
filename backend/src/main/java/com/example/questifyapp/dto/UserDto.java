package com.example.questifyapp.dto;

import com.example.questifyapp.enums.UserRole;

public record UserDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        UserRole role
) {
}
