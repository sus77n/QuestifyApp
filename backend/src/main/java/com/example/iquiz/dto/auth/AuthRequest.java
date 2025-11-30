package com.example.iquiz.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Authentication request containing login credentials")
@Data
public class AuthRequest {


    @Schema(example = "admin", description = "Username or email of the user")
    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username too long (max 50 chars)")
    private String usernameOrEmail;


    @Schema(example = "su123456", description = "User's password")
    @NotBlank(message = "Password cannot be blank")
//    @Size(min = 8, max = 40, message = "Password must be 8-40 characters")
    private String password;

}
