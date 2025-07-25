package com.example.iquiz.controller;

import com.example.iquiz.dto.AuthResponse;
import com.example.iquiz.dto.AuthRequest;
import com.example.iquiz.dto.SignupRequest;
import com.example.iquiz.enums.UserRole;
import com.example.iquiz.security.UserDetailsImpl;
import com.example.iquiz.service.AuthService;
import com.example.iquiz.service.CustomUserDetailsService;
import com.example.iquiz.utility.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.registerUser(signupRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(
            @Valid @RequestBody AuthRequest authRequest,
            HttpServletResponse response) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsernameOrEmail(),
                            authRequest.getPassword()
                    )
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService
                    .loadUserByUsername(authRequest.getUsernameOrEmail());


            final Instant issuedAt = Instant.now();
            final Instant expiration = issuedAt.plusMillis(jwtUtil.getExpiration());
            final String jwtToken = jwtUtil.generateToken(userDetails);


            AuthResponse authResponse = new AuthResponse(
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getFirstName(),
                    userDetails.getLastName(),
                    jwtToken,
                    expiration,
                    userDetails.getCreatedAt(),
                    UserRole.valueOf(
                            userDetails.getAuthorities().stream()
                                    .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                                    .findFirst()
                                    .orElse("USER")
                    )
            );

            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
            response.setHeader("X-Content-Type-Options", "no-sniff");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CACHE_CONTROL, "no-store")
                    .body(authResponse);

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid username or password", e);
        } catch (DisabledException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "User account is disabled", e);
        } catch (LockedException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "User account is locked", e);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Login failed", e);
        }

    }

    @GetMapping("/user")
    public ResponseEntity<AuthResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        AuthResponse userResponse = new AuthResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                null, // Token is not needed here
                null, // Token expiration is not needed here
                userDetails.getCreatedAt(),
                UserRole.valueOf(
                        userDetails.getAuthorities().stream()
                                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                                .findFirst()
                                .orElse("USER")
                )
        );

        return ResponseEntity.ok(userResponse);
    }
}