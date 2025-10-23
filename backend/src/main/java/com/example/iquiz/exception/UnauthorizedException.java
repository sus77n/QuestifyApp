package com.example.iquiz.exception;

public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message) {
        super(message, ErrorCode.UNAUTHORIZED);
    }
}
