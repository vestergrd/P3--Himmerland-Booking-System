package com.auu_sw3_6.Himmerland_booking_software.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.auu_sw3_6.Himmerland_booking_software.api.model.ErrorResponse;
import com.auu_sw3_6.Himmerland_booking_software.service.UserService;

@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(UserService.class);

  // API error exception handling

  @ExceptionHandler(IllegalBookingException.class)
  public ResponseEntity<ErrorResponse> handleIllegalBookingException(IllegalBookingException ex) {
    log.error("Illegal booking: " + ex.getMessage());
    ErrorResponse errorResponse = new ErrorResponse("Illegal booking", HttpStatus.BAD_REQUEST);
    errorResponse
        .setDetails(Map.of("reason", ex.getMessage(), "errorcode", String.valueOf(ex.getError().getErrorCode())));
    return ResponseEntity.status(errorResponse.getStatus()).contentType(MediaType.APPLICATION_JSON).body(errorResponse);
  }

  @ExceptionHandler(BookingNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleBookingNotFoundException(BookingNotFoundException ex) {
    log.error("Booking not found: " + ex.getMessage());
    ErrorResponse errorResponse = new ErrorResponse("Booking not found", HttpStatus.NOT_FOUND);
    errorResponse.setDetails(Map.of("reason", ex.getMessage()));
    return ResponseEntity.status(errorResponse.getStatus()).contentType(MediaType.APPLICATION_JSON).body(errorResponse);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
    log.error("Endpoint not found: " + ex.getMessage());
    ErrorResponse errorResponse = new ErrorResponse("Endpoint not found", HttpStatus.NOT_FOUND);
    errorResponse.setDetails(Map.of("reason", "The requested endpoint does not exist"));
    return ResponseEntity.status(errorResponse.getStatus()).contentType(MediaType.APPLICATION_JSON).body(errorResponse);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
    log.error("Access denied: " + ex.getMessage());
    ErrorResponse errorResponse = new ErrorResponse("Access denied", HttpStatus.FORBIDDEN);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body(errorResponse);
  }

  @ExceptionHandler(RestrictedUsernameException.class)
  public ResponseEntity<ErrorResponse> handleRestrictedUsernameException(RestrictedUsernameException ex) {
    log.error("Username: " + ex.getMessage() + " is not allowed");
    ErrorResponse errorResponse = new ErrorResponse("Username: \"" + ex.getMessage() + "\" is not allowed",
        HttpStatus.CONFLICT);
    return ResponseEntity.status(ex.getStatus()).contentType(MediaType.APPLICATION_JSON).body(errorResponse);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
    log.error("User with username " + ex.getMessage() + " already exists");
    ErrorResponse errorResponse = new ErrorResponse(
        "User with username: \"" + ex.getMessage() + "\" already exists",
        HttpStatus.CONFLICT);
    return ResponseEntity.status(ex.getStatus()).contentType(MediaType.APPLICATION_JSON).body(errorResponse);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
    log.error(ex.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(),
        ex.getStatus());
    return ResponseEntity.status(ex.getStatus()).contentType(MediaType.APPLICATION_JSON).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    log.error("Validation error occurred: " + ex.getMessage());
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(errors);
  }

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<String> handleDatabaseExceptions(DataAccessException ex) {
    log.error("Database error occurred: " + ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
        .body("Database error occurred: " + ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGenericExceptions(Exception ex) {
    log.error("An error occurred: " + ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
        .body("An error occurred: " + ex.getMessage());
  }

}
