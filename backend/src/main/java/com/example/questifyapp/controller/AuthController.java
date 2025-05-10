package com.example.questifyapp.controller;

import com.example.questifyapp.entity.AuthenticationRequest;
import com.example.questifyapp.entity.User;
import com.example.questifyapp.repository.UserRepository;
import com.example.questifyapp.service.CustomUserDetailsService;
import com.example.questifyapp.service.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
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
    public String registerUser(@RequestBody User user) {
        System.out.println("Registering user: " + user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
            return "User registered successfully";
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return jwt;
    }

}