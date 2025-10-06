package com.auu_sw3_6.Himmerland_booking_software.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import com.auu_sw3_6.Himmerland_booking_software.api.model.ErrorResponse;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
public void testHandleAccessDeniedException() {
    // Act
    ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAccessDeniedException(new AccessDeniedException("Access denied"));

    // Assert
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Status should be FORBIDDEN");
    assertNotNull(response.getBody(), "Response body should not be null");
    assertEquals("Access denied", response.getBody().getMessage(), "Error message should be 'Access denied'");
}

    @Test
    public void testHandleDatabaseExceptions() {
        // Act
        ResponseEntity<String> response = globalExceptionHandler.handleDatabaseExceptions(new DataAccessException("Database error") {});

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status should be INTERNAL_SERVER_ERROR");
        assertTrue(response.getBody().contains("Database error"), "Response body should contain 'Database error'");
    }

    @Test
    public void testHandleUserAlreadyExistsException_HandledByGenericExceptionHandler() {
        // Act
        ResponseEntity<String> response = globalExceptionHandler.handleGenericExceptions(new UserAlreadyExistsException("existingUser"));

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status should be INTERNAL_SERVER_ERROR");
        assertTrue(response.getBody().contains("existingUser"), "Response body should contain the username 'existingUser'");
    }

    @Test
    public void testHandleGenericExceptions() {
        // Act
        ResponseEntity<String> response = globalExceptionHandler.handleGenericExceptions(new Exception("Unexpected error"));

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status should be INTERNAL_SERVER_ERROR");
        assertTrue(response.getBody().contains("Unexpected error"), "Response body should contain 'Unexpected error'");
    }
}

