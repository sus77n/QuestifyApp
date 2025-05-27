package com.example.questifyapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {

    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username too long (max 50 chars)")
    private String usernameOrEmail;


    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 40, message = "Password must be 8-40 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password requires: 1 digit, 1 lowercase, 1 uppercase, 1 special character"
    )
    private String password;

}
