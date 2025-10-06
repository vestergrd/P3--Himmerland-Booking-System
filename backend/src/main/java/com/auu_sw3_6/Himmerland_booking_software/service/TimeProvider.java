package com.auu_sw3_6.Himmerland_booking_software.service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Component;

@Component
public class TimeProvider {

    public LocalDate getToday() {
        return LocalDate.now();
    }

    public LocalTime getNow() {
        return LocalTime.now();
    }
}
