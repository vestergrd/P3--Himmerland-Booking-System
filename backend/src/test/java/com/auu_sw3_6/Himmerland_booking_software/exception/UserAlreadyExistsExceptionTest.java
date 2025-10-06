package com.auu_sw3_6.Himmerland_booking_software.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsExceptionTest {

    @Test
    public void testUserAlreadyExistsException() {
        // Arrange
        String message = "existingUsername";

        // Act
        UserAlreadyExistsException exception = new UserAlreadyExistsException(message);

        // Assert
        assertEquals(message, exception.getMessage(), "Exception message should match the provided message");
        assertEquals(HttpStatus.CONFLICT, exception.getStatus(), "HTTP status should be CONFLICT");
    }
}
