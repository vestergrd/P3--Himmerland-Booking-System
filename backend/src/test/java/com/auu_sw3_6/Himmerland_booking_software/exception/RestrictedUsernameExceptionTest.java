package com.auu_sw3_6.Himmerland_booking_software.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class RestrictedUsernameExceptionTest {

    @Test
    public void testRestrictedUsernameException() {
        // Arrange
        String message = "restrictedUsername";

        // Act
        RestrictedUsernameException exception = new RestrictedUsernameException(message);

        // Assert
        assertEquals(message, exception.getMessage(), "Exception message should match the provided message");
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus(), "HTTP status should be FORBIDDEN");
    }
}
