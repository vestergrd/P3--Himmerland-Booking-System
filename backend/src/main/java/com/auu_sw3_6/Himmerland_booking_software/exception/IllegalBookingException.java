package com.auu_sw3_6.Himmerland_booking_software.exception;

public class IllegalBookingException extends RuntimeException {

  private final BookingError error;

  public IllegalBookingException(BookingError error) {
      super(error.getErrorMessage());
      this.error = error;
  }

  public BookingError getError() {
    return error;
  }
}
