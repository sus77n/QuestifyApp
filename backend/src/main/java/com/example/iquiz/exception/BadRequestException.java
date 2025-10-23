package com.example.iquiz.exception;

public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(message, ErrorCode.BAD_REQUEST);
    }
}