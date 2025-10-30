package com.example.iquiz.exception;

public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super(message, ErrorCode.FORBIDDEN);
    }
}
