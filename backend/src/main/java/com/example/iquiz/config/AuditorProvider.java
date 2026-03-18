package com.example.iquiz.config;

import com.example.iquiz.security.UserDetailsImpl;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuditorProvider implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl user) {
            return Optional.of(user.getId());
        }

        return Optional.empty();
    }
}