package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import java.lang.reflect.Field;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Booking;
import com.auu_sw3_6.Himmerland_booking_software.api.model.BookingDetails;
import com.auu_sw3_6.Himmerland_booking_software.api.model.ErrorResponse;
import com.auu_sw3_6.Himmerland_booking_software.api.model.User;
import com.auu_sw3_6.Himmerland_booking_software.exception.UserNotFoundException;
import com.auu_sw3_6.Himmerland_booking_software.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RequestMapping("api/user")
public abstract class UserController<T extends User> {

  protected final UserService<T> userService;

  @Autowired
  public UserController(UserService<T> userService) {
    this.userService = userService;
  }

  @PutMapping("/{updatedUserId}")
  public ResponseEntity<User> updateUser(@PathVariable Long updatedUserId, @RequestBody User updatedUser) {
    User user = userService.updateUser(updatedUserId, updatedUser);
    return ResponseEntity.ok(user);
  }

  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TENANT')")
  @GetMapping(produces = "application/json")
  public ResponseEntity<Object> getUser() {
    try {
      User user = userService.getAuthenticatedUser();
      return ResponseEntity.ok(user);
    } catch (UserNotFoundException e) {
      System.out.println("User not found" + e.getMessage());
      return new ErrorResponse("User not found", HttpStatus.NOT_FOUND).send();
    } catch (Exception e) {
      System.out.println("An unexpected error occurred" + e.getMessage());
      return new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR).send();
    }
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping("{id}")
  public ResponseEntity<Object> getUserById(@PathVariable Long id) {
    Optional<T> user = userService.getUserById(id);
    if (user.isPresent()) {
      return ResponseEntity.ok(user.get());
    }
    return new ErrorResponse("User not found", HttpStatus.NOT_FOUND).send();
  }

  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TENANT')")
  @GetMapping("profilePicture")
  public ResponseEntity<Object> getProfilePicture() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    Optional<byte[]> imageBytesOptional = userService.getProfilePictureByUsername(username);
    if (imageBytesOptional.isPresent()) {
      return ResponseEntity.ok()
          .contentType(MediaType.IMAGE_JPEG)
          .body(imageBytesOptional.get());
    }
    return new ErrorResponse("Profile picture not found", HttpStatus.NOT_FOUND).send();
  }

  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TENANT')")
  @PostMapping(value = "book-resource", produces = "application/json")
  public ResponseEntity<?> createBooking(@RequestBody BookingDetails details) {
    System.out.println("Creating booking");
    logObjectAttributes(details);
    Booking booking = userService.createBooking(details);
    return ResponseEntity.ok(booking);
  }

  public static void logObjectAttributes(Object obj) {
    if (obj == null) {
      System.out.println("Object is null.");
      return;
    }

    Class<?> objClass = obj.getClass();
    System.out.println("Logging attributes of " + objClass.getName());

    for (Field field : objClass.getDeclaredFields()) {
      field.setAccessible(true);
      try {
        Object value = field.get(obj);
        System.out.println(field.getName() + ": " + value);
      } catch (IllegalAccessException e) {
        System.out.println("Could not access field " + field.getName());
      }
    }
  }

}
