package com.example.iquiz.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.NoSuchElementException;

@Component
public class ExceptionTranslator {

    public ApiException translate(Exception ex) {
        if (ex instanceof ApiException apiEx) {
            return apiEx;
        }
        if (ex instanceof EntityNotFoundException || ex instanceof NoSuchElementException) {
            return new ResourceNotFoundException("Resource", "unknown", "N/A");
        }
        if (ex instanceof IllegalArgumentException || ex instanceof MethodArgumentNotValidException) {
            return new BadRequestException("Invalid request or input");
        }
        if (ex instanceof AccessDeniedException) {
            return new ForbiddenException("Access denied");
        }
        if (ex instanceof AuthenticationException || ex instanceof AuthenticationServiceException) {
            return new UnauthorizedException("Authentication failed");
        }
        if (ex instanceof DataIntegrityViolationException) {
            return new ConflictException("Data integrity violation");
        }
        // fallback for everything else
        return new ApiException("Unexpected error: " + ex.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR, ex);
    }
}
