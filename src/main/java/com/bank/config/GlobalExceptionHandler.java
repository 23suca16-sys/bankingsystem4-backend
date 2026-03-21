package com.bank.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception exception) {
        return ResponseEntity.ok(Map.of(
                "message", "Request processed with an error",
                "error", exception.getMessage() != null ? exception.getMessage() : "Unexpected error"
        ));
    }
}
