package com.auu_sw3_6.Himmerland_booking_software.api.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookingDateTest {

    @Test
    void constructor_ShouldInitializeFieldsCorrectly() {
        // Arrange
        LocalDate expectedDate = LocalDate.of(2024, 12, 2);
        Long expectedAmount = 5L;

        // Act
        BookingDate bookingDate = new BookingDate(expectedDate, expectedAmount);

        // Assert
        assertEquals(expectedDate, bookingDate.getDate());
        assertEquals(expectedAmount, bookingDate.getAmount());
    }

    @Test
    void getDate_ShouldReturnCorrectDate() {
        // Arrange
        LocalDate expectedDate = LocalDate.of(2024, 12, 2);
        BookingDate bookingDate = new BookingDate(expectedDate, 5L);

        // Act
        LocalDate actualDate = bookingDate.getDate();

        // Assert
        assertEquals(expectedDate, actualDate);
    }

    @Test
    void setDate_ShouldUpdateDateCorrectly() {
        // Arrange
        LocalDate initialDate = LocalDate.of(2024, 12, 2);
        LocalDate newDate = LocalDate.of(2024, 12, 3);
        BookingDate bookingDate = new BookingDate(initialDate, 5L);

        // Act
        bookingDate.setDate(newDate);

        // Assert
        assertEquals(newDate, bookingDate.getDate());
    }

    @Test
    void getAmount_ShouldReturnCorrectAmount() {
        // Arrange
        Long expectedAmount = 10L;
        BookingDate bookingDate = new BookingDate(LocalDate.now(), expectedAmount);

        // Act
        Long actualAmount = bookingDate.getAmount();

        // Assert
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void setAmount_ShouldUpdateAmountCorrectly() {
        // Arrange
        Long initialAmount = 5L;
        Long newAmount = 10L;
        BookingDate bookingDate = new BookingDate(LocalDate.now(), initialAmount);

        // Act
        bookingDate.setAmount(newAmount);

        // Assert
        assertEquals(newAmount, bookingDate.getAmount());
    }

    @Test
    void setDate_ShouldHandleNullValues() {
        // Arrange
        BookingDate bookingDate = new BookingDate(LocalDate.now(), 5L);

        // Act
        bookingDate.setDate(null);

        // Assert
        assertNull(bookingDate.getDate());
    }

    @Test
    void setAmount_ShouldHandleNullValues() {
        // Arrange
        BookingDate bookingDate = new BookingDate(LocalDate.now(), 5L);

        // Act
        bookingDate.setAmount(null);

        // Assert
        assertNull(bookingDate.getAmount());
    }
}
