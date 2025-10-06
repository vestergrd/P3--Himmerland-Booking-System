package com.auu_sw3_6.Himmerland_booking_software.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IllegalBookingExceptionTest {

    @Test
    void constructor_ShouldSetErrorAndMessageCorrectly() {
        // Arrange
        BookingError expectedError = BookingError.INVALID_DATE_RANGE;

        // Act
        IllegalBookingException exception = new IllegalBookingException(expectedError);

        // Assert
        assertEquals(expectedError, exception.getError());
        assertEquals(expectedError.getErrorMessage(), exception.getMessage());
    }

    @Test
    void getError_ShouldReturnCorrectBookingError() {
        // Arrange
        BookingError expectedError = BookingError.USER_NOT_FOUND;

        // Act
        IllegalBookingException exception = new IllegalBookingException(expectedError);

        // Assert
        assertEquals(expectedError, exception.getError());
    }

    @Test
    void getMessage_ShouldReturnErrorMessageFromBookingError() {
        // Arrange
        BookingError error = BookingError.TOO_MANY_BOOKINGS;

        // Act
        IllegalBookingException exception = new IllegalBookingException(error);

        // Assert
        assertEquals(error.getErrorMessage(), exception.getMessage());
    }
}
