package com.auu_sw3_6.Himmerland_booking_software.api.model;

import java.time.LocalDate;

public class BookingDate {
    private LocalDate date;
    private Long amount;

    public BookingDate(LocalDate date, Long amount) {
        this.date = date;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
