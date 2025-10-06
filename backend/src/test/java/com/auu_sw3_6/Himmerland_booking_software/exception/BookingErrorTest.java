package com.auu_sw3_6.Himmerland_booking_software.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingErrorTest {

    @Test
    void enumValues_ShouldHaveCorrectErrorMessageAndCode() {
        // Assert each enum's error message and error code
        assertEquals("Startdato skal være før slutdato, og perioden må ikke overstige maksimum tilladt dage.", BookingError.INVALID_DATE_RANGE.getErrorMessage());
        assertEquals(1001, BookingError.INVALID_DATE_RANGE.getErrorCode());

        assertEquals("Startdato kan ikke være tidligere end dags dato.", BookingError.START_DATE_IN_PAST.getErrorMessage());
        assertEquals(1002, BookingError.START_DATE_IN_PAST.getErrorCode());

        assertEquals("Slutdato kan ikke være tidligere end dags dato.", BookingError.END_DATE_IN_PAST.getErrorMessage());
        assertEquals(1003, BookingError.END_DATE_IN_PAST.getErrorCode());

        assertEquals("Afhentningstidspunkt skal være i fremtiden for startdatoen.", BookingError.PICKUP_TIME_IN_PAST.getErrorMessage());
        assertEquals(1006, BookingError.PICKUP_TIME_IN_PAST.getErrorCode());

        assertEquals("Afleveringstidspunkt skal være i fremtiden for slutdatoen.", BookingError.DROPOFF_TIME_IN_PAST.getErrorMessage());
        assertEquals(1004, BookingError.DROPOFF_TIME_IN_PAST.getErrorCode());

        assertEquals("Booking på weekender er ikke tilladt.", BookingError.WEEKEND_BOOKING_NOT_ALLOWED.getErrorMessage());
        assertEquals(1005, BookingError.WEEKEND_BOOKING_NOT_ALLOWED.getErrorCode());

        assertEquals("Bruger ikke fundet", BookingError.USER_NOT_FOUND.getErrorMessage());
        assertEquals(2001, BookingError.USER_NOT_FOUND.getErrorCode());

        assertEquals("For mange aktive bookinger for denne ressource.", BookingError.TOO_MANY_BOOKINGS.getErrorMessage());
        assertEquals(2002, BookingError.TOO_MANY_BOOKINGS.getErrorCode());

        assertEquals("For mange bookinger på en periode for denne ressource.", BookingError.TOO_OFTEN_BOOKING.getErrorMessage());
        assertEquals(2003, BookingError.TOO_OFTEN_BOOKING.getErrorCode());

        assertEquals("Kan ikke redigere afsluttet booking.", BookingError.COMPLETED_BOOKING_EDIT.getErrorMessage());
        assertEquals(2004, BookingError.COMPLETED_BOOKING_EDIT.getErrorCode());

        assertEquals("Kan ikke redigere annulleret booking.", BookingError.CANCELLED_BOOKING_EDIT.getErrorMessage());
        assertEquals(2005, BookingError.CANCELLED_BOOKING_EDIT.getErrorCode());

        assertEquals("Kan ikke redigere forpasset booking.", BookingError.LATE_BOOKING_EDIT.getErrorMessage());
        assertEquals(2006, BookingError.LATE_BOOKING_EDIT.getErrorCode());

        assertEquals("Kan ikke ændre starttiden for aktiv booking.", BookingError.ACTIVE_BOOKING_START_EDIT.getErrorMessage());
        assertEquals(2007, BookingError.ACTIVE_BOOKING_START_EDIT.getErrorCode());

        assertEquals("Ressourcen er ikke tilgængelig for de valgte datoer.", BookingError.DEFAULT_ERROR.getErrorMessage());
        assertEquals(2008, BookingError.DEFAULT_ERROR.getErrorCode());

        assertEquals("Brugeren har ikke tilladelse til at redigere denne booking.", BookingError.USER_NOT_ALLOWED.getErrorMessage());
        assertEquals(2009, BookingError.USER_NOT_ALLOWED.getErrorCode());
    }

    @Test
    void enumConstants_ShouldBeAccessible() {
        // Assert the enum constants are not null
        assertNotNull(BookingError.INVALID_DATE_RANGE);
        assertNotNull(BookingError.START_DATE_IN_PAST);
        assertNotNull(BookingError.PICKUP_TIME_IN_PAST);
        assertNotNull(BookingError.DROPOFF_TIME_IN_PAST);
        assertNotNull(BookingError.WEEKEND_BOOKING_NOT_ALLOWED);
        assertNotNull(BookingError.USER_NOT_FOUND);
        assertNotNull(BookingError.TOO_MANY_BOOKINGS);
        assertNotNull(BookingError.TOO_OFTEN_BOOKING);
        assertNotNull(BookingError.COMPLETED_BOOKING_EDIT);
        assertNotNull(BookingError.CANCELLED_BOOKING_EDIT);
        assertNotNull(BookingError.LATE_BOOKING_EDIT);
        assertNotNull(BookingError.ACTIVE_BOOKING_START_EDIT);
        assertNotNull(BookingError.DEFAULT_ERROR);
        assertNotNull(BookingError.USER_NOT_ALLOWED);
    }
}
