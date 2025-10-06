package com.auu_sw3_6.Himmerland_booking_software.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class BookingNotFoundExceptionTest {

    @Test
    void constructor_ShouldSetMessageAndDefaultStatus() {
        // Arrange
        String expectedMessage = "Booking not found";

        // Act
        BookingNotFoundException exception = new BookingNotFoundException(expectedMessage);

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getMessage_ShouldReturnNullIfNoMessageProvided() {
        // Act
        BookingNotFoundException exception = new BookingNotFoundException(null);

        // Assert
        assertNull(exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void getStatus_ShouldAlwaysReturnNotFound() {
        // Act
        BookingNotFoundException exception = new BookingNotFoundException("Some message");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}
