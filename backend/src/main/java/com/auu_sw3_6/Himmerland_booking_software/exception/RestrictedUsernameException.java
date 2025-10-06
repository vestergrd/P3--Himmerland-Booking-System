package com.auu_sw3_6.Himmerland_booking_software.exception;

import org.springframework.http.HttpStatus;

public class RestrictedUsernameException extends RuntimeException {

  private final HttpStatus status;

  public RestrictedUsernameException(String message) {
    super(message);
    this.status = HttpStatus.FORBIDDEN;
  }

  public HttpStatus getStatus() {
    return status;
  }

}
