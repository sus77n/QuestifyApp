package com.example.iquiz.exception;

public class ConflictException extends ApiException {
    public ConflictException(String message) {
        super(message, ErrorCode.CONFLICT);
    }
}