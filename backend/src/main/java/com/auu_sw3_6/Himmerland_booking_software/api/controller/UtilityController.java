package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Utility;
import com.auu_sw3_6.Himmerland_booking_software.service.BookingService;
import com.auu_sw3_6.Himmerland_booking_software.service.UtilityService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/utility")
public class UtilityController extends ResourceController<Utility> {

  private final UtilityService utilityService;
  private final BookingService bookingService;

  @Autowired
  public UtilityController(UtilityService utilityService, BookingService bookingService) {
    super(utilityService, bookingService);
    this.utilityService = utilityService;
    this.bookingService = bookingService;
  }

  @PostMapping(value = "/create", consumes = { "multipart/form-data" })
  @Operation(summary = "Create new utility", description = "This endpoint allows you to create a new utility.")
  public ResponseEntity<Utility> createUtility(@RequestPart("utility") Utility utility,
      @RequestPart("resourcePictures") MultipartFile resourcePictures) {
    Utility createdUtility = utilityService.createUtility(utility, resourcePictures);
    return ResponseEntity.ok(createdUtility);
  }
}