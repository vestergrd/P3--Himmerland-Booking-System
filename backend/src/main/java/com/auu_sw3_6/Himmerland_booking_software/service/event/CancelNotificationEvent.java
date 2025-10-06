package com.auu_sw3_6.Himmerland_booking_software.service.event;


import com.auu_sw3_6.Himmerland_booking_software.api.model.Booking;
import org.springframework.context.ApplicationEvent;

public class CancelNotificationEvent extends ApplicationEvent {
    private final Booking booking;

    public CancelNotificationEvent(Object source, Booking booking) {
        super(source);
        this.booking = booking;
    }

    public Booking getBooking() {
        return booking;
    }
}
