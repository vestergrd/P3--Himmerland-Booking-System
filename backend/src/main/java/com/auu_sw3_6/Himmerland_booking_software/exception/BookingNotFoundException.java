package com.auu_sw3_6.Himmerland_booking_software.exception;

import org.springframework.http.HttpStatus;

public class BookingNotFoundException extends RuntimeException {

  private final HttpStatus status;

  public BookingNotFoundException(String message) {
      super(message);
      this.status = HttpStatus.NOT_FOUND;
    }

  public HttpStatus getStatus() {
    return status;
  }

}
