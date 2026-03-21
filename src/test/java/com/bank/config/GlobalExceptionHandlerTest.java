package com.bank.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleException_withMessage_returnsExpectedPayload() {
        ResponseEntity<Map<String, String>> response = handler.handleException(new RuntimeException("failure"));

        assertNotNull(response.getBody());
        assertEquals("Request processed with an error", response.getBody().get("message"));
        assertEquals("failure", response.getBody().get("error"));
    }

    @Test
    void handleException_withoutMessage_returnsFallbackError() {
        ResponseEntity<Map<String, String>> response = handler.handleException(new RuntimeException());

        assertNotNull(response.getBody());
        assertEquals("Unexpected error", response.getBody().get("error"));
    }
}