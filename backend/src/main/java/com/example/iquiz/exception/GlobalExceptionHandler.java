package com.example.iquiz.exception;

import com.example.iquiz.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ExceptionTranslator translator;

    /**
     * Handles all known ApiExceptions thrown manually or translated automatically.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<?>> handleApiException(ApiException ex) {
        HttpStatus status = mapToHttpStatus(ex.getErrorCode());
        return ResponseEntity.status(status)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    /**
     * Handles all unexpected exceptions and uses ExceptionTranslator
     * to map them into a proper ApiException subclass.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAnyException(Exception ex) {
        ApiException apiEx = translator.translate(ex);
        HttpStatus status = mapToHttpStatus(apiEx.getErrorCode());

        return ResponseEntity.status(status)
                .body(ApiResponse.error(apiEx.getMessage(), apiEx.getErrorCode()));
    }

    /**
     * Maps custom ErrorCode enums to standard HTTP status codes.
     */
    private HttpStatus mapToHttpStatus(ErrorCode code) {
        return switch (code) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case CONFLICT -> HttpStatus.CONFLICT;
            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}