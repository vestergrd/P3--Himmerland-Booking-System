package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Hospitality;
import com.auu_sw3_6.Himmerland_booking_software.service.BookingService;
import com.auu_sw3_6.Himmerland_booking_software.service.HospitalityService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/hospitality")
public class HospitalityController extends ResourceController<Hospitality> {

  private final HospitalityService hospitalityService;
  private final BookingService bookingService;

  @Autowired
  public HospitalityController(HospitalityService hospitalityService, BookingService bookingService) {
    super(hospitalityService, bookingService);
    this.hospitalityService = hospitalityService;
    this.bookingService = bookingService;
  }

  @PostMapping(value = "/create", consumes = { "multipart/form-data" })
  @Operation(summary = "Create new hospitality resource", description = "This endpoint allows you to create a new hospitality resource.")
  public ResponseEntity<Hospitality> createHospitality(@RequestPart("hospitality") Hospitality hospitality,
      @RequestPart("resourcePictures") MultipartFile resourcePictures) {
    Hospitality createdHospitality = hospitalityService.createHospitality(hospitality, resourcePictures);
    return ResponseEntity.ok(createdHospitality);
  }

}