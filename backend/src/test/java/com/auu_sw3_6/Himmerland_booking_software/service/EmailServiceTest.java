package com.auu_sw3_6.Himmerland_booking_software.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class EmailServiceTest {

    private static final String TEST_RECEIVER = "test@example.com";
    private static final String TEST_USER = "Test User";
    private static final String TEST_RESOURCE = "Test Resource";
    private static final String TEST_TIMERANGE = "10:00 - 12:00";
    private static final LocalDate TEST_DATE = LocalDate.of(2024, 12, 10);

    @Test
    void testSendPickupNotification() {
        // Act & Assert: Ensure no exceptions are thrown when calling the method
        assertDoesNotThrow(() -> 
            EmailService.sendPickupNotification(TEST_RECEIVER, TEST_USER, TEST_RESOURCE, TEST_TIMERANGE),
            "Sending pickup notification should not throw any exceptions"
        );
    }

    @Test
    void testSendDropoffNotification() {
        // Act & Assert: Ensure no exceptions are thrown when calling the method
        assertDoesNotThrow(() -> 
            EmailService.sendDropoffNotification(TEST_RECEIVER, TEST_USER, TEST_RESOURCE, TEST_TIMERANGE),
            "Sending dropoff notification should not throw any exceptions"
        );
    }

    @Test
    void testSendMissedDropoffNotification() {
        // Act & Assert: Ensure no exceptions are thrown when calling the method
        assertDoesNotThrow(() -> 
            EmailService.sendMissedDropoffNotification(TEST_RECEIVER, TEST_USER, TEST_RESOURCE, TEST_TIMERANGE, TEST_DATE),
            "Sending missed dropoff notification should not throw any exceptions"
        );
    }

    @Test
    void testSendCancelledBookingNotification() {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 12, 10);
        LocalDate endDate = LocalDate.of(2024, 12, 15);

        // Act & Assert: Ensure no exceptions are thrown when calling the method
        assertDoesNotThrow(() -> 
            EmailService.sendCancelledBookingNotification(TEST_RECEIVER, TEST_USER, TEST_RESOURCE, startDate, endDate),
            "Sending cancelled booking notification should not throw any exceptions"
        );
    }
}
