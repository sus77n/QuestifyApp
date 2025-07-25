package com.example.iquiz.dto;

import com.example.iquiz.enums.UserRole;

public record UserDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        UserRole role
) {
}
