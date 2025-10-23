package com.example.iquiz.dto;

import com.example.iquiz.exception.ErrorCode;
import lombok.Data;

@Data
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private ErrorCode errorCode;

    private ApiResponse(boolean success, String message, T data, ErrorCode errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> error(String message, ErrorCode code) {
        return new ApiResponse<>(false, message, null, code);
    }

}

