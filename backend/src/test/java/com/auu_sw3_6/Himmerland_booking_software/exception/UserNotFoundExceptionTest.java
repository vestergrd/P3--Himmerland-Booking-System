package com.auu_sw3_6.Himmerland_booking_software.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class UserNotFoundExceptionTest {

    @Test
    public void testUserNotFoundException() {
        // Arrange
        String message = "User not found";

        // Act
        UserNotFoundException exception = new UserNotFoundException(message);

        // Assert
        assertEquals(message, exception.getMessage(), "Exception message should match the provided message");
    }
}
