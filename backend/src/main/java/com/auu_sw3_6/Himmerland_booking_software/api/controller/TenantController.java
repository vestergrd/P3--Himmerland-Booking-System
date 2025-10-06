package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Booking;
import com.auu_sw3_6.Himmerland_booking_software.api.model.BookingDetails;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Tenant;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.BookingStatus;
import com.auu_sw3_6.Himmerland_booking_software.service.TenantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/tenant")
public class TenantController extends UserController<Tenant> {

  private final TenantService tenantService;

  @Autowired
  public TenantController(TenantService tenantService) {
    super(tenantService);
    this.tenantService = tenantService;
  }

  @PermitAll
  @PostMapping(value = "register", consumes = { "multipart/form-data" })
  @Operation(summary = "Register a new user", description = "This endpoint allows you to add a new user to the system. "
      +
      "You must provide the user's name, email, and mobile number. " +
      "The id and profile picture path will be handled automatically.")
  public ResponseEntity<Object> createUser(
      @Parameter(description = "User details") @RequestPart("user") @Valid Tenant user,
      @Parameter(description = "User's profile picture (optional)") @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {

    Tenant createdUser = tenantService.createTenant(user, profilePicture);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  @DeleteMapping(value = "/deleteTenant/{id}")
  @Operation(summary = "Delete tenant", description = "This endpoint deletes a tenant.")
  public ResponseEntity<Void> deleteTenant(@PathVariable Long id, HttpServletResponse response) {
    tenantService.softDeleteTenant(id);

    // Clear the JWT cookie
    ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0) // Expire the cookie immediately
        .sameSite("none")
        .build();

    response.addHeader("Set-Cookie", jwtCookie.toString());

    return ResponseEntity.noContent().build();
  }

  @PutMapping(value = "/updateTenant", consumes = { "multipart/form-data" })
  @Operation(summary = "Update Tenant", description = "This endpoint updates a tenant.")
  public ResponseEntity<Tenant> updateTenant(
    @RequestPart("user") Tenant tenant, 
    @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture, 
    HttpServletResponse response) {
    Tenant updatedTenant = tenantService.updateUser(tenant, profilePicture);
    
    // Clear the JWT cookie
    ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0) // Expire the cookie immediately
        .sameSite("none") // only for development, maybe
        .build();

    response.addHeader("Set-Cookie", jwtCookie.toString());
    
    return ResponseEntity.ok(updatedTenant);
  }

  @PutMapping(value = "/editBooking/{id}", produces = "application/json")
  @Operation(summary = "Edit booking", description = "This endpoint edits a booking.")
  public ResponseEntity<Booking> editBooking(@PathVariable Long id, @RequestBody BookingDetails bookingDetails) {
    Booking updatedBooking = tenantService.editBooking(id, bookingDetails);
    return ResponseEntity.ok(updatedBooking);
  }

  @GetMapping(value = "/getOwnBookings", produces = "application/json")
  @Operation(summary = "Get own bookings", description = "This endpoint retrieves all bookings for the current user.")
  public ResponseEntity<List<Booking>> getOwnBookings() {
    List<Booking> bookings = tenantService.getOwnBookings();
    return ResponseEntity.ok(bookings);
  }

  @PutMapping("/cancelBooking/{bookingId}")
  @Operation(summary = "Set booking status to canceled", description = "Update the status of a booking to 'CANCELED' by its ID")
  public ResponseEntity<Void> setBookingStatusAsCanceled(@PathVariable long bookingId) {
    tenantService.setBookingStatus(bookingId, BookingStatus.CANCELED);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/test")
  @PreAuthorize("hasRole('TENANT')")
  public ResponseEntity<String> test() {
    return ResponseEntity.ok("Test");
  }

}
