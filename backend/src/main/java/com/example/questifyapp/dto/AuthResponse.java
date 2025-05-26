package com.example.questifyapp.dto;

import com.example.questifyapp.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private String token;
    private Instant issuedAt;
    private Instant expiresAt;

    private Long id;
    private String username;
    private String email;
    private UserRole role;
}
