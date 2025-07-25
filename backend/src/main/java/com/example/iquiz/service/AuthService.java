package com.example.iquiz.service;

import com.example.iquiz.dto.SignupRequest;
import com.example.iquiz.entity.User;
import com.example.iquiz.enums.UserRole;
import com.example.iquiz.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<String> registerUser(SignupRequest signupRequest) {

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        try {
            User user = new User();
            user.setUsername(signupRequest.getUsername());
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
            user.setEmail(signupRequest.getEmail());
            user.setRole(UserRole.STUDENT);

            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok("Registered user successfully");
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
