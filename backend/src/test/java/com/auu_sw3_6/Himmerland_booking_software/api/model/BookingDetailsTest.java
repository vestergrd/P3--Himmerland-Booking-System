package com.auu_sw3_6.Himmerland_booking_software.api.model;

import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.ResourceType;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.TimeRange;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookingDetailsTest {

    @Test
    void constructor_ShouldInitializeFieldsCorrectly() {
        // Arrange
        Long resourceID = 1L;
        ResourceType resourceType = ResourceType.UTILITY; // Adjust if needed
        LocalDate startDate = LocalDate.of(2024, 11, 7);
        LocalDate endDate = LocalDate.of(2024, 11, 8);
        TimeRange pickupTime = TimeRange.EARLY;
        TimeRange dropoffTime = TimeRange.LATE;
        String receiverName = "John Doe";
        String handoverName = "Jane Doe";

        // Act
        BookingDetails bookingDetails = new BookingDetails(resourceID, resourceType, startDate, endDate, pickupTime, dropoffTime, receiverName, handoverName);

        // Assert
        assertEquals(resourceID, bookingDetails.getResourceID());
        assertEquals(resourceType, bookingDetails.getResourceType());
        assertEquals(startDate, bookingDetails.getStartDate());
        assertEquals(endDate, bookingDetails.getEndDate());
        assertEquals(pickupTime, bookingDetails.getPickupTime());
        assertEquals(dropoffTime, bookingDetails.getDropoffTime());
        assertEquals(receiverName, bookingDetails.getReceiverName());
        assertEquals(handoverName, bookingDetails.getHandoverName());
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        BookingDetails bookingDetails = new BookingDetails();
        Long resourceID = 2L;
        ResourceType resourceType = ResourceType.UTILITY;
        LocalDate startDate = LocalDate.of(2024, 12, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 2);
        TimeRange pickupTime = TimeRange.LATE;
        TimeRange dropoffTime = TimeRange.EARLY;
        String receiverName = "Alice";
        String handoverName = "Bob";

        // Act
        bookingDetails.setResourceID(resourceID);
        bookingDetails.setResourceType(resourceType);
        bookingDetails.setStartDate(startDate);
        bookingDetails.setEndDate(endDate);
        bookingDetails.setPickupTime(pickupTime);
        bookingDetails.setDropoffTime(dropoffTime);
        bookingDetails.setReceiverName(receiverName);
        bookingDetails.setHandoverName(handoverName);

        // Assert
        assertEquals(resourceID, bookingDetails.getResourceID());
        assertEquals(resourceType, bookingDetails.getResourceType());
        assertEquals(startDate, bookingDetails.getStartDate());
        assertEquals(endDate, bookingDetails.getEndDate());
        assertEquals(pickupTime, bookingDetails.getPickupTime());
        assertEquals(dropoffTime, bookingDetails.getDropoffTime());
        assertEquals(receiverName, bookingDetails.getReceiverName());
        assertEquals(handoverName, bookingDetails.getHandoverName());
    }

    @Test
    void setNullValues_ShouldHandleGracefully() {
        // Arrange
        BookingDetails bookingDetails = new BookingDetails();

        // Act
        bookingDetails.setResourceID(null);
        bookingDetails.setResourceType(null);
        bookingDetails.setStartDate(null);
        bookingDetails.setEndDate(null);
        bookingDetails.setPickupTime(null);
        bookingDetails.setDropoffTime(null);
        bookingDetails.setReceiverName(null);
        bookingDetails.setHandoverName(null);

        // Assert
        assertNull(bookingDetails.getResourceID());
        assertNull(bookingDetails.getResourceType());
        assertNull(bookingDetails.getStartDate());
        assertNull(bookingDetails.getEndDate());
        assertNull(bookingDetails.getPickupTime());
        assertNull(bookingDetails.getDropoffTime());
        assertNull(bookingDetails.getReceiverName());
        assertNull(bookingDetails.getHandoverName());
    }

    @Test
    void timeRange_ShouldReturnCorrectValues() {
        // Assert
        assertEquals("7:00-7:30", TimeRange.EARLY.getTimeSlot());
        assertEquals("7:00-7:30", TimeRange.EARLY.toValue());
        assertEquals("07:00", TimeRange.EARLY.getStartTime().toString());
        assertEquals("07:30", TimeRange.EARLY.getEndTime().toString());
        assertEquals("11:00-12:00", TimeRange.LATE.getTimeSlot());
        assertEquals("11:00-12:00", TimeRange.LATE.toValue());
        assertEquals("11:00", TimeRange.LATE.getStartTime().toString());
        assertEquals("12:00", TimeRange.LATE.getEndTime().toString());
    }

    @Test
    void timeRange_FromString_ShouldReturnCorrectEnum() {
        // Act & Assert
        assertEquals(TimeRange.EARLY, TimeRange.fromString("7:00-7:30"));
        assertEquals(TimeRange.LATE, TimeRange.fromString("11:00-12:00"));

        // Exception case
        Exception exception = assertThrows(IllegalArgumentException.class, () -> TimeRange.fromString("invalid-time"));
        assertEquals("Unknown time slot: invalid-time", exception.getMessage());
    }
}
