package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Booking;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Resource;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.BookingStatus;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.ResourceType;
import com.auu_sw3_6.Himmerland_booking_software.service.BookingService;

import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("api/booking")

public class BookingController {

  private final BookingService bookingService;

  @Autowired
  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @Operation(summary = "Get all bookings", description = "Retrieve a list of all bookings")
  @GetMapping(value = "/get-all", produces = "application/json")
  public ResponseEntity<List<Booking>> getBookings() {
    List<Booking> bookings = bookingService.getAllBookings();
    if (bookings.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } else {
      return ResponseEntity.ok(bookings);
    }
  }

  @Operation(summary = "Delete a booking", description = "Remove a booking from the system by its ID")
  @DeleteMapping("/delete/{bookingId}")
  public ResponseEntity<Void> deleteBooking(@PathVariable long bookingId) {
    boolean isDeleted = bookingService.deleteBooking(bookingId);
    if (isDeleted) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @PutMapping("/{bookingId}/receiver-name")
    public ResponseEntity<Void> updateReceiverName(@PathVariable long bookingId, @RequestParam String receiverName) {
        bookingService.setReceiverName(bookingId, receiverName);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{bookingId}/handover-name")
    public ResponseEntity<Void> updateHandoverName(@PathVariable long bookingId, @RequestParam String handoverName) {
        bookingService.setHandoverName(bookingId, handoverName);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{bookingId}/late-status")
    @Operation(summary = "Mark booking as late", description = "Update the status of a booking to 'LATE' by its ID")
    public ResponseEntity<Void> markBookingAsLate(@PathVariable long bookingId) {
      bookingService.lateBookingStatus(bookingId);
      return ResponseEntity.ok().build();
    }

    @PutMapping("/{bookingId}/cancel")
    @Operation(summary = "Set booking status to canceled", description = "Update the status of a booking to 'CANCELED' by its ID")
    public ResponseEntity<Void> setBookingStatusAsCanceled(@PathVariable long bookingId) {
      bookingService.setBookingStatus(bookingId, BookingStatus.CANCELED, 1, true);
      return ResponseEntity.ok().build();
    }

    @PutMapping("/{resourceType}/{resourceId}/cancel-bookings-for-resource")
    @Operation(summary = "Cancel bookings for a resource", description = "Set all booking status to 'CANCELED' by resource id and type")
    public ResponseEntity<Void> cancelBookings(@PathVariable long resourceId, @PathVariable ResourceType resourceType) {
      bookingService.cancelConfirmedAndPendingBookingsForResource(resourceId, resourceType);
      return ResponseEntity.ok().build();
    }
}