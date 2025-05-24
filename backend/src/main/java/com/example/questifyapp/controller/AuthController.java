package com.example.questifyapp.controller;

import com.example.questifyapp.dto.AuthResponse;
import com.example.questifyapp.dto.AuthenticationRequest;
import com.example.questifyapp.entity.User;
import com.example.questifyapp.entity.UserRole;
import com.example.questifyapp.repository.UserRepository;
import com.example.questifyapp.service.CustomUserDetailsService;
import com.example.questifyapp.service.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody String username, String password, String email) {
        registerUser(username, password, email);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(
            @Valid @RequestBody AuthenticationRequest authenticationRequest,
            HttpServletResponse response) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );

            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(authenticationRequest.getUsername());

            final String jwtToken = jwtUtil.generateToken(userDetails);
            final Instant expiration = Instant.now().plusMillis(jwtUtil.getExpiration());


            AuthResponse authResponse = AuthResponse.builder()
                    .token(jwtToken)
                    .issuedAt(Instant.now())
                    .expiresAt(expiration)
                    .username(userDetails.getUsername())
                    .build();

            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
            response.setHeader("X-Content-Type-Options", "nosniff");

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

}