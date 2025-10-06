package com.auu_sw3_6.Himmerland_booking_software.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminNotFouldExceptionTest {

    @Test
    void constructor_ShouldSetMessageCorrectly() {
        // Arrange
        String expectedMessage = "Admin not found";

        // Act
        AdminNotFouldException exception = new AdminNotFouldException(expectedMessage);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getMessage_ShouldReturnNullIfNoMessageProvided() {
        // Act
        AdminNotFouldException exception = new AdminNotFouldException(null);

        // Assert
        assertNull(exception.getMessage());
    }
}
