package com.example.iquiz.controller;

import com.example.iquiz.dto.*;
import com.example.iquiz.exception.UnauthorizedException;
import com.example.iquiz.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public com.example.iquiz.dto.ApiResponse<Void> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        authService.registerUser(signupRequest);
        return ApiResponse.success(null, "User registered successfully");
    }

    @Operation(
            summary = "User Login",
            description = "Authenticate a user and return a JWT token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "User credentials for authentication",
                    content = @Content(
                            schema = @Schema(implementation = AuthRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Admin Example",
                                            summary = "Login as admin",
                                            value = """
                                                    {
                                                      "usernameOrEmail": "admin",
                                                      "password": "su123456"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "User Example",
                                            summary = "Login as regular user",
                                            value = """
                                                    {
                                                      "usernameOrEmail": "minh",
                                                      "password": "su123456"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200", description = "Login successful"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401", description = "Invalid credentials"
                    )
            }
    )
    @PostMapping("/login")
    public ApiResponse<AuthResponse> loginUser(
            @Valid @RequestBody AuthRequest authRequest,
            HttpServletResponse response) {
        AuthResponse authResponse = authService.loginUser(authRequest, response);
        return ApiResponse.success(authResponse, "Login successful");
    }

    @GetMapping("/me")
    public ApiResponse<UserDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("Unauthorized: Please login first");
        }
        UserDto currentUser = authService.getCurrentUser(userDetails);
        return ApiResponse.success(currentUser, "User profile fetched");
    }
}
