package com.example.iquiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {

    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username too long (max 50 chars)")
    private String usernameOrEmail;


    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 40, message = "Password must be 8-40 characters")
    private String password;

}
