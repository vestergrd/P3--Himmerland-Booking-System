package com.auu_sw3_6.Himmerland_booking_software.api.model;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {

  @JsonProperty("message")
  private String message;

  @JsonProperty("status")
  private int status;

  @JsonProperty("timestamp")
  private String timestamp;

  @JsonProperty("details")
  private Map<String, String> details;

  private HttpStatus httpStatus;

  public ErrorResponse(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
    this.status = httpStatus.value();
    this.timestamp = String.valueOf(System.currentTimeMillis());
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public Map<String, String> getDetails() {
    return details;
  }

  public void setDetails(Map<String, String> details) {
    this.details = details;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public void setHttpStatus(HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
  }

  public ResponseEntity<Object> send() {
    return ResponseEntity.status(httpStatus)
        .header("Content-Type", "application/json")
        .body(this);
  }

}
