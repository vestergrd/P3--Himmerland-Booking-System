package com.auu_sw3_6.Himmerland_booking_software.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends RuntimeException {
  private final HttpStatus status;

  public UserAlreadyExistsException(String message) {
    super(message);
    this.status = HttpStatus.CONFLICT;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
