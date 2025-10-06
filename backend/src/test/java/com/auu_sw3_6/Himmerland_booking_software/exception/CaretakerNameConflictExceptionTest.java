package com.auu_sw3_6.Himmerland_booking_software.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CaretakerNameConflictExceptionTest {

    @Test
    void constructor_ShouldSetMessageCorrectly() {
        // Arrange
        String expectedMessage = "Caretaker name conflict occurred";

        // Act
        CaretakerNameConflictException exception = new CaretakerNameConflictException(expectedMessage);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void getMessage_ShouldReturnNullIfNoMessageProvided() {
        // Act
        CaretakerNameConflictException exception = new CaretakerNameConflictException(null);

        // Assert
        assertNull(exception.getMessage());
    }
}
