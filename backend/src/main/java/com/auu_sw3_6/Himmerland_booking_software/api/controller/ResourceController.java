package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.BookingDate;
import com.auu_sw3_6.Himmerland_booking_software.api.model.ErrorResponse;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Resource;
import com.auu_sw3_6.Himmerland_booking_software.exception.ResourceNotFoundException;
import com.auu_sw3_6.Himmerland_booking_software.service.BookingService;
import com.auu_sw3_6.Himmerland_booking_software.service.ResourceService;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.ResourceType;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/resource")
public abstract class ResourceController<T extends Resource> {

  protected final ResourceService<T> resourceService;
  protected final BookingService bookingService;

  @Autowired
  public ResourceController(ResourceService<T> resourceService, BookingService bookingService) {
    this.resourceService = resourceService;
    this.bookingService = bookingService;
  }

  @Operation(summary = "Get a resource by ID", description = "Retrieve details of a specific resource by its ID")
  @GetMapping(value = "/{id}", produces = "application/json")
  public ResponseEntity<T> getResourceById(@PathVariable Long id) {
    try {
      Optional<T> resource = resourceService.getResourceById(id);
      return resource.map(ResponseEntity::ok)
          .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    } catch (ResourceNotFoundException e) {
      System.out.println("No resources found: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (Exception e) {
      System.out.println("An unexpected error occurred: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Operation(summary = "Get all resources", description = "Retrieve a list of all available resources")
  @GetMapping(value = "/get-all", produces = "application/json")
  public ResponseEntity<List<T>> getResources() {
    List<T> resources = resourceService.getAllResources();
    if (resources.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } else {
      return ResponseEntity.ok(resources);
    }
  }

  @Operation(summary = "Get confirmed booked dates for a resource", description = "Retrieve an array of dates, along with the amount booked on that date when a specific resource is booked")
  @GetMapping(value = "/{id}/booked-dates", produces = "application/json")
  public ResponseEntity<List<BookingDate>> getReservedDates(@PathVariable Long id) {
    Resource resource = resourceService.getResourceById(id).orElse(null);
    List<BookingDate> reservedDates = bookingService.getBookedDatesWithAmount(resource);
    return ResponseEntity.ok(reservedDates);
  }

  @Operation(summary = "Delete a resource", description = "Remove a resource from the system by its ID and type")
  @DeleteMapping("/api/resource/{resourceType}/{resourceId}")
  public ResponseEntity<Void> deleteResource(@PathVariable long resourceId, @PathVariable ResourceType resourceType) {
    bookingService.cancelConfirmedAndPendingBookingsForResource(resourceId, resourceType);
    boolean isDeleted = resourceService.softDeleteResource(resourceId);
    if (isDeleted) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @Operation(summary = "Update a resource", description = "Update the details of an existing resource")
  @PutMapping(value = "/update", consumes = { "multipart/form-data" })
  public ResponseEntity<T> updateResource(
    @RequestPart("updatedResource") T updatedResource,
    @RequestPart(value = "pictureFile", required = false) MultipartFile pictureFile) {
    T resource = resourceService.updateResource(updatedResource, pictureFile);
    return ResponseEntity.ok(resource);
  }

  @GetMapping(value = "/{id}/picture", produces = "application/json")
  @Operation(summary = "Get resource picture", description = "This endpoint returns the id of a resource from the resource id.")
  public ResponseEntity<Object> getResourcePicture(@PathVariable long id) {
    Optional<byte[]> imageBytesOptional = resourceService.getresourcePicturesByResourceId(id);
    if (imageBytesOptional.isPresent()) {
      return ResponseEntity.ok()
          .contentType(MediaType.IMAGE_JPEG)
          .body(imageBytesOptional.get());
    }
    return new ErrorResponse("Resource picture not found", HttpStatus.NOT_FOUND).send();
  }
}