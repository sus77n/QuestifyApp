package com.example.iquiz.exception;

public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s not found with %s = '%s'", resource, field, value),
                ErrorCode.NOT_FOUND);
    }
}
