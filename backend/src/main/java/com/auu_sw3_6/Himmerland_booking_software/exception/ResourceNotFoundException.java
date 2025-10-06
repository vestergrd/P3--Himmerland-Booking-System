package com.auu_sw3_6.Himmerland_booking_software.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RuntimeException {

  private final HttpStatus status;

  public ResourceNotFoundException(String message) {
      super(message);
      this.status = HttpStatus.NOT_FOUND;
    }

  public HttpStatus getStatus() {
    return status;
  }

}
