package com.example.questifyapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
//    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

}
