package com.example.questifyapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationRequest {

    @NotBlank(message = "Username is required")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    private String password;

}
