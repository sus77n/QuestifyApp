package com.example.iquiz.utility;

import com.example.iquiz.entity.User;
import com.example.iquiz.exception.ApiException;
import com.example.iquiz.exception.ErrorCode;
import com.example.iquiz.exception.ResourceNotFoundException;
import com.example.iquiz.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {

    @Autowired
    private UserRepository userRepository;

    public User getUserFromAuthContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;

        if (username == null) {
            throw new ApiException("No authenticated user found", ErrorCode.UNAUTHORIZED);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return user;
    }
}
