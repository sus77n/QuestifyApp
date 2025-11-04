package com.example.iquiz.service;

import com.example.iquiz.dto.AuthRequest;
import com.example.iquiz.dto.AuthResponse;
import com.example.iquiz.dto.SignupRequest;
import com.example.iquiz.dto.UserDto;
import com.example.iquiz.entity.User;
import com.example.iquiz.enums.UserRole;
import com.example.iquiz.exception.ApiException;
import com.example.iquiz.exception.ErrorCode;
import com.example.iquiz.exception.UnauthorizedException;
import com.example.iquiz.exception.UserAlreadyExistsException;
import com.example.iquiz.mapper.UserMapper;
import com.example.iquiz.repository.UserRepository;
import com.example.iquiz.utility.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.iquiz.security.UserDetailsImpl;

import java.time.Instant;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtils jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Transactional
    public void registerUser(SignupRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(UserRole.STUDENT);

        userRepository.save(user);
    }

    @Transactional
    public AuthResponse loginUser(AuthRequest authRequest, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsernameOrEmail(),
                            authRequest.getPassword()
                    )
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService
                    .loadUserByUsername(authRequest.getUsernameOrEmail());

            Instant issuedAt = Instant.now();
            Instant expiration = issuedAt.plusMillis(jwtUtil.getExpiration());
            String jwtToken = jwtUtil.generateToken(userDetails);

            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
            response.setHeader(HttpHeaders.PRAGMA, "no-cache");
            response.setHeader(HttpHeaders.EXPIRES, "0");
            response.setHeader("X-Content-Type-Options", "no-sniff");

            return new AuthResponse(
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

        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid username or password");
        } catch (DisabledException e) {
            throw new ApiException("User account is disabled", ErrorCode.FORBIDDEN, e);
        } catch (LockedException e) {
            throw new ApiException("User account is locked", ErrorCode.FORBIDDEN, e);
        } catch (AuthenticationException e) {
            throw new ApiException("Authentication failed", ErrorCode.UNAUTHORIZED, e);
        } catch (Exception e) {
            throw new ApiException("Unexpected error during login", ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    public UserDto getCurrentUser(UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return userMapper.toDto(user);
    }
}
