package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Tool;
import com.auu_sw3_6.Himmerland_booking_software.service.BookingService;
import com.auu_sw3_6.Himmerland_booking_software.service.ToolService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/tool")
public class ToolController extends ResourceController<Tool> {

  private final ToolService toolService;
  private final BookingService bookingService;

  @Autowired
  public ToolController(ToolService toolService, BookingService bookingService) {
    super(toolService, bookingService);
    this.toolService = toolService;
    this.bookingService = bookingService;
  }

  @PostMapping(value = "/create", consumes = { "multipart/form-data" })
  @Operation(summary = "Create new tool", description = "This endpoint allows you to create a new tool.")
  public ResponseEntity<Tool> createTool(@RequestPart("tool") Tool tool,
      @RequestPart("resourcePictures") MultipartFile resourcePictures) {
    Tool createdTool = toolService.createTool(tool, resourcePictures);
    return ResponseEntity.ok(createdTool);
  }
}