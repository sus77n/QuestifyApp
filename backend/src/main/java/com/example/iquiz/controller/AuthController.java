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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Void> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        authService.registerUser(signupRequest);
        return ApiResponse.success(null, "User registered successfully");
    }

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
