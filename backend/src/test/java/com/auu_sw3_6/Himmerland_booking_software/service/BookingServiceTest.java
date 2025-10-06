package com.auu_sw3_6.Himmerland_booking_software.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Booking;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Resource;
import com.auu_sw3_6.Himmerland_booking_software.api.model.User;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.BookingStatus;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.TimeRange;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.BookingRepository;
import com.auu_sw3_6.Himmerland_booking_software.exception.BookingError;
import com.auu_sw3_6.Himmerland_booking_software.exception.IllegalBookingException;
import com.auu_sw3_6.Himmerland_booking_software.exception.ResourceNotFoundException;
import com.auu_sw3_6.Himmerland_booking_software.service.event.CancelNotificationEvent;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

  @Mock
  protected TimeProvider timeProvider;

  @Mock
  private BookingRepository bookingRepository;

  @Mock
  private ResourceServiceFactory resourceServiceFactory;

  @Mock
  private ResourceService<Resource> resourceService;

  @InjectMocks
  private BookingService bookingService;

  private Booking booking;
  private Resource resource;
  private User user;
  private LocalDate today;
  private LocalTime now;

  private static class TestUser extends User {

  }

  private static class TestResource extends Resource {

  }

  @BeforeEach
  public void setUp() {

    today = LocalDate.of(2024, 11, 4);
    now = LocalTime.of(10, 5);
    user = new TestUser();
    user.setId(1L);

    resource = new TestResource();
    resource.setId(1L);
    resource.setCapacity(5);

    booking = new Booking();
    booking.setId(1L);
    booking.setUser(user);
    booking.setResource(resource);
    booking.setStartDate(today.plusDays(1));
    booking.setEndDate(today.plusDays(3));
    booking.setStatus(BookingStatus.PENDING);
  }

  @Test
  public void testCreateBooking_shouldSaveAndReturnBooking() {
    // Arrange
    when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

    // Act
    Booking result = bookingService.createBooking(booking);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(bookingRepository).save(booking);
  }

  @Test
  public void testGetAllBookings_shouldReturnListOfBookings() {
    // Arrange
    List<Booking> bookings = List.of(booking);
    when(bookingRepository.findAll()).thenReturn(bookings);

    // Act
    List<Booking> result = bookingService.getAllBookings();

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(bookingRepository).findAll();
  }

  @Test
  public void testGetBookingById_shouldReturnBooking() {
    // Arrange
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    // Act
    Booking result = bookingService.getBookingById(1L);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(bookingRepository).findById(1L);
  }

  @Test
  public void testGetBookingById_shouldReturnNullWhenBookingNotFound() {
    // Arrange
    when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

    // Act
    Booking result = bookingService.getBookingById(1L);

    // Assert
    assertNull(result);
    verify(bookingRepository).findById(1L);
  }

  @Test
  public void testDeleteBooking_shouldReturnTrueWhenDeleted() {
    // Arrange
    when(bookingRepository.existsById(1L)).thenReturn(true);

    // Act
    boolean result = bookingService.deleteBooking(1L);

    // Assert
    assertTrue(result);
    verify(bookingRepository).existsById(1L);
    verify(bookingRepository).deleteById(1L);
  }

  @Test
  public void testDeleteBooking_shouldReturnFalseWhenBookingNotFound() {
    // Arrange
    when(bookingRepository.existsById(1L)).thenReturn(false);

    // Act
    boolean result = bookingService.deleteBooking(1L);

    // Assert
    assertFalse(result);
    verify(bookingRepository).existsById(1L);
    verify(bookingRepository, never()).deleteById(anyLong());
  }

  @Test
  public void testSetReceiverName_shouldUpdateReceiverName() {
    // Arrange
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    // Act
    bookingService.setReceiverName(1L, "Receiver Name");

    // Assert
    assertEquals("Receiver Name", booking.getReceiverName());
    assertEquals(BookingStatus.COMPLETED, booking.getStatus());
    verify(bookingRepository).save(booking);
  }

  @Test
  public void testSetReceiverName_shouldThrowExceptionForInvalidName() {
    // Arrange
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      bookingService.setReceiverName(1L, ""); // Invalid receiver name
    });

    assertEquals("Receiver name cannot be null or empty.", exception.getMessage());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  public void testSetReceiverName_shouldThrowResourceNotFoundExceptionWhenBookingNotFound() {
    // Arrange
    when(bookingRepository.findById(1L)).thenReturn(Optional.empty()); // Mock repository to return empty Optional

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      bookingService.setReceiverName(1L, "Valid Name"); // Use a valid receiver name
    });

    assertEquals("Booking not found with ID: 1", exception.getMessage());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  public void testCancelPendingBookings_shouldCancelPendingBookingsWhenCapacityExceeded() {
    // Arrange
    when(timeProvider.getToday()).thenReturn(today);

    ApplicationEventPublisher mockEventPublisher = mock(ApplicationEventPublisher.class);
    bookingService = new BookingService(bookingRepository, resourceServiceFactory, mockEventPublisher, timeProvider);

    resource.setCapacity(1);

    Booking lateBooking = new Booking();
    lateBooking.setId(2L);
    lateBooking.setResource(resource);
    lateBooking.setStatus(BookingStatus.LATE);

    Booking pendingBooking = new Booking();
    pendingBooking.setId(3L);
    pendingBooking.setResource(resource);
    pendingBooking.setStartDate(today);
    pendingBooking.setEndDate(today.plusDays(2));
    pendingBooking.setStatus(BookingStatus.PENDING);

    Booking confirmedBooking = new Booking();
    confirmedBooking.setId(4L);
    confirmedBooking.setResource(resource);
    confirmedBooking.setStatus(BookingStatus.CONFIRMED);

    when(bookingRepository.findByStatus(BookingStatus.LATE)).thenReturn(List.of(lateBooking));
    when(bookingRepository.findByStatus(BookingStatus.PENDING))
        .thenReturn(new ArrayList<>(List.of(pendingBooking)));
    when(bookingRepository.findByStatus(BookingStatus.CONFIRMED)).thenReturn(List.of(confirmedBooking));

    // Act
    bookingService.cancelPendingBookings();

    // Assert
    assertEquals(BookingStatus.CANCELED, pendingBooking.getStatus(),
        "Pending booking was not canceled as expected.");
    verify(bookingRepository).save(pendingBooking);
    verify(mockEventPublisher).publishEvent(any(CancelNotificationEvent.class));
  }

  @Test
  public void testCancelPendingBookings_shouldNotCancelPendingBookingsWhenCapacityNotExceeded() {
    // Arrange

    when(timeProvider.getToday()).thenReturn(today);

    Booking pendingBooking = new Booking();
    pendingBooking.setId(3L);
    pendingBooking.setResource(resource);
    pendingBooking.setStartDate(today);
    pendingBooking.setStatus(BookingStatus.PENDING);

    when(bookingRepository.findByStatus(BookingStatus.LATE)).thenReturn(new ArrayList<>());
    when(bookingRepository.findByStatus(BookingStatus.PENDING))
        .thenReturn(new ArrayList<>(List.of(pendingBooking)));
    when(bookingRepository.findByStatus(BookingStatus.CONFIRMED)).thenReturn(new ArrayList<>());

    // Act
    bookingService.cancelPendingBookings();

    assertEquals(BookingStatus.PENDING, pendingBooking.getStatus());
    verify(bookingRepository, never()).save(pendingBooking);
  }

  @Test
  public void testCreateBooking_Successful() {
    // Arrange
    Booking inputBooking = new Booking();
    inputBooking.setId(1L);
    when(bookingRepository.save(inputBooking)).thenReturn(inputBooking);

    // Act
    Booking result = bookingService.createBooking(inputBooking);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(bookingRepository).save(inputBooking);
  }

  @Test
  public void testCreateBooking_RepositoryReturnsNull() {
    // Arrange
    Booking inputBooking = new Booking();
    inputBooking.setId(1L);

    when(bookingRepository.save(inputBooking)).thenReturn(null);

    // Act
    Booking result = bookingService.createBooking(inputBooking);

    // Assert
    assertNull(result);
    verify(bookingRepository).save(inputBooking);
  }

  @Test
  public void isWeekend_shouldReturnTrueForWeekend() {
    // Arrange
    LocalDate saturday = LocalDate.of(2021, 9, 4); // Saturday
    LocalDate sunday = LocalDate.of(2021, 9, 5); // Sunday

    // Act
    boolean result = bookingService.isWeekend(saturday);
    boolean result2 = bookingService.isWeekend(sunday);

    // Assert
    assertTrue(result);
    assertTrue(result2);
  }

  @Test
  public void isWeekend_shouldReturnFalseForWeekday() {
    // Arrange
    LocalDate monday = LocalDate.of(2021, 9, 6);
    LocalDate tuesday = LocalDate.of(2021, 9, 7);
    LocalDate wednesday = LocalDate.of(2021, 9, 8);
    LocalDate thursday = LocalDate.of(2021, 9, 9);
    LocalDate friday = LocalDate.of(2021, 9, 10);

    // Act
    boolean result = bookingService.isWeekend(monday);
    boolean result2 = bookingService.isWeekend(tuesday);
    boolean result3 = bookingService.isWeekend(wednesday);
    boolean result4 = bookingService.isWeekend(thursday);
    boolean result5 = bookingService.isWeekend(friday);

    // Assert
    assertFalse(result);
    assertFalse(result2);
    assertFalse(result3);
    assertFalse(result4);
    assertFalse(result5);
  }

  @Test
  public void isResourceAvailable_shouldReturnTrueWhenNoOverlappingBookings() {

    // Arrange
    resource.setCapacity(1);
    booking.setStartDate(today.plusDays(7));
    booking.setEndDate(today.plusDays(10));

    Booking booking2 = new Booking();
    booking2.setId(2L);
    booking2.setUser(user);
    booking2.setResource(resource);
    booking2.setStartDate(today.plusDays(4));
    booking2.setEndDate(today.plusDays(6));
    booking2.setStatus(BookingStatus.PENDING);

    Booking booking3 = new Booking();
    booking3.setId(3L);
    booking3.setUser(user);
    booking3.setResource(resource);
    booking3.setStartDate(today.plusDays(1));
    booking3.setEndDate(today.plusDays(3));
    booking3.setStatus(BookingStatus.PENDING);

    List<Booking> bookings;
    bookings = Arrays.asList(booking2, booking3);

    // Act
    boolean result = bookingService.isResourceAvailable(bookings, resource, booking.getStartDate(),
        booking.getEndDate());

    // Assert
    assertTrue(result);
  }

  @Test
  public void isResourceAvailable_shouldRetrunFalse_WhenOverlappingBookings() {

    // Arrange
    resource.setCapacity(1);
    booking.setStartDate(today.plusDays(2));
    booking.setEndDate(today.plusDays(5));

    Booking booking2 = new Booking();
    booking2.setId(2L);
    booking2.setUser(user);
    booking2.setResource(resource);
    booking2.setStartDate(today.plusDays(4));
    booking2.setEndDate(today.plusDays(6));
    booking2.setStatus(BookingStatus.PENDING);

    Booking booking3 = new Booking();
    booking3.setId(3L);
    booking3.setUser(user);
    booking3.setResource(resource);
    booking3.setStartDate(today.plusDays(1));
    booking3.setEndDate(today.plusDays(3));
    booking3.setStatus(BookingStatus.PENDING);

    List<Booking> bookings;
    bookings = Arrays.asList(booking2, booking3);

    // Act
    boolean result = bookingService.isResourceAvailable(bookings, resource, booking.getStartDate(),
        booking.getEndDate());

    // Assert
    assertFalse(result);

  }

  @Test
  public void calculateOverlappingDays_shouldReturnZeroForNonOverlappingDates() {
    // Arrange
    LocalDate bookingStart = today.plusDays(1);
    LocalDate bookingEnd = today.plusDays(3);
    LocalDate rangeStart = today.plusDays(4);
    LocalDate rangeEnd = today.plusDays(6);

    // Act
    long result = bookingService.calculateOverlappingDays(bookingStart, bookingEnd, rangeStart, rangeEnd);

    // Assert
    assertEquals(0, result);
  }

  @Test
  public void calculateOverlappingDays_shouldReturnCorrectOverlappingDays() {
    // Arrange
    LocalDate bookingStart = today.plusDays(1);
    LocalDate bookingEnd = today.plusDays(3);
    LocalDate rangeStart = today.plusDays(2);
    LocalDate rangeEnd = today.plusDays(4);

    // Act
    long result = bookingService.calculateOverlappingDays(bookingStart, bookingEnd, rangeStart, rangeEnd);

    // Assert
    assertEquals(2, result);
  }

  @Test
  public void checkBookingPeriodValidity_shouldThrowIllegalBookingExceptionForStartDateAfterEndDate() {
    // Arrange

    LocalDate startDate = today.plusDays(5);
    LocalDate endDate = today.plusDays(3);
    TimeRange pickupTime = TimeRange.EARLY;
    TimeRange dropoffTime = TimeRange.LATE;

    // Act & Assert
    IllegalBookingException exception = assertThrows(IllegalBookingException.class, () -> {
      bookingService.checkBookingPeriodValidity(false, today, now, startDate, endDate, pickupTime, dropoffTime);
    });

    assertEquals(BookingError.INVALID_DATE_RANGE, exception.getError());
  }

  @Test
  public void checkBookingPeriodValidity_shouldThrowIllegalBookingExceptionForExceedingMAX_BOOKING_DAYS() {
    // Arrange
    LocalDate startDate = today.plusDays(1);
    LocalDate endDate = today.plusDays(30);
    TimeRange pickupTime = TimeRange.EARLY;
    TimeRange dropoffTime = TimeRange.LATE;

    // Act & Assert
    IllegalBookingException exception = assertThrows(IllegalBookingException.class, () -> {
      bookingService.checkBookingPeriodValidity(false, today, now, startDate, endDate, pickupTime, dropoffTime);
    });

    assertEquals(BookingError.INVALID_DATE_RANGE, exception.getError());
  }

  @Test
  public void checkBookingPeriodValidity_shouldThrowIllegalBookingExceptionForStartDateInPast() {
    // Arrange
    LocalDate startDate = today.minusDays(1);
    LocalDate endDate = today.plusDays(3);
    TimeRange pickupTime = TimeRange.EARLY;
    TimeRange dropoffTime = TimeRange.LATE;

    // Act & Assert
    IllegalBookingException exception = assertThrows(IllegalBookingException.class, () -> {
      bookingService.checkBookingPeriodValidity(false, today, now, startDate, endDate, pickupTime, dropoffTime);
    });

    assertEquals(BookingError.START_DATE_IN_PAST, exception.getError());
  }

  @Test
  public void checkBookingPeriodValidity_shouldThrowIllegalBookingExceptionForPickupTimeInPast() {
    // Arrange
    LocalDate startDate = today;
    LocalDate endDate = today.plusDays(3);
    TimeRange pickupTime = TimeRange.EARLY;
    TimeRange dropoffTime = TimeRange.LATE;

    now = LocalTime.of(15, 0, 0);

    // Act & Assert
    IllegalBookingException exception = assertThrows(IllegalBookingException.class, () -> {
      bookingService.checkBookingPeriodValidity(false, today, now, startDate, endDate, pickupTime, dropoffTime);
    });

    assertEquals(BookingError.PICKUP_TIME_IN_PAST, exception.getError());
  }

  @Test
  public void checkBookingPeriodValidity_shouldThrowIllegalBookingExceptionForEndDateInPast() {
    // Arrange
    LocalDate startDate = today.minusDays(5);
    LocalDate endDate = today.minusDays(2);
    TimeRange pickupTime = TimeRange.EARLY;
    TimeRange dropoffTime = TimeRange.LATE;

    now = LocalTime.of(15, 0, 0);

    // Act & Assert
    IllegalBookingException exception = assertThrows(IllegalBookingException.class, () -> {
      bookingService.checkBookingPeriodValidity(true, today, now, startDate, endDate, pickupTime, dropoffTime);
    });

    assertEquals(BookingError.END_DATE_IN_PAST, exception.getError());
  }

  @Test
  public void checkBookingPeriodValidity_shouldThrowIllegalBookingExceptionForDropoffTimeInPast() {
    // Arrange
    LocalDate startDate = today.minusDays(3);
    LocalDate endDate = today;
    TimeRange pickupTime = TimeRange.EARLY;
    TimeRange dropoffTime = TimeRange.LATE;

    now = LocalTime.of(15, 0, 0);

    // Act & Assert
    IllegalBookingException exception = assertThrows(IllegalBookingException.class, () -> {
      bookingService.checkBookingPeriodValidity(true, today, now, startDate, endDate, pickupTime, dropoffTime);
    });

    assertEquals(BookingError.DROPOFF_TIME_IN_PAST, exception.getError());
  }

  @Test
  public void checkBookingPeriodValidity_shouldThrowIllegalBookingExceptionForWeekendBooking() {
    // Arrange
    LocalDate startDate = today.plusDays(4);
    LocalDate endDate = today.plusDays(6);
    TimeRange pickupTime = TimeRange.EARLY;
    TimeRange dropoffTime = TimeRange.LATE;

    // Act & Assert
    IllegalBookingException exception = assertThrows(IllegalBookingException.class, () -> {
      bookingService.checkBookingPeriodValidity(false, today, now, startDate, endDate, pickupTime, dropoffTime);
    });

    assertEquals(BookingError.WEEKEND_BOOKING_NOT_ALLOWED, exception.getError());
  }

  @Test
  public void checkBookingPeriodValidity_shouldNotThrowExceptionForValidNewBookingPeriod() {
    // Arrange
    LocalDate startDate = today.plusDays(1);
    LocalDate endDate = today.plusDays(3);
    TimeRange pickupTime = TimeRange.EARLY;
    TimeRange dropoffTime = TimeRange.LATE;

    // Act & Assert
    bookingService.checkBookingPeriodValidity(false, today, now, startDate, endDate, pickupTime, dropoffTime);
  }

  public void checkBookingPeriodValidity_shouldNotThrowExceptionForValidEditBookingPeriod() {
    // Arrange
    LocalDate startDate = today.plusDays(1);
    LocalDate endDate = today.plusDays(3);
    TimeRange pickupTime = TimeRange.EARLY;
    TimeRange dropoffTime = TimeRange.LATE;

    // Act & Assert
    bookingService.checkBookingPeriodValidity(true, today, now, startDate, endDate, pickupTime, dropoffTime);
  }

  @Test
public void testUpdateBooking_shouldSaveAndReturnUpdatedBooking() {
    // Arrange
    Booking updatedBooking = new Booking();
    updatedBooking.setId(1L);
    updatedBooking.setUser(user);
    updatedBooking.setResource(resource);
    updatedBooking.setStartDate(today.plusDays(2));
    updatedBooking.setEndDate(today.plusDays(4));
    updatedBooking.setStatus(BookingStatus.CONFIRMED);

    when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);

    // Act
    Booking result = bookingService.updateBooking(updatedBooking);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(BookingStatus.CONFIRMED, result.getStatus());
    verify(bookingRepository).save(updatedBooking);
}
  
  @Test
  public void testUpdateBooking_shouldThrowResourceNotFoundExceptionWhenBookingNotFound() {
      // Arrange
      Booking nonExistentBooking = new Booking();
      nonExistentBooking.setId(99L);
      nonExistentBooking.setUser(user);
      nonExistentBooking.setResource(resource);
      nonExistentBooking.setStartDate(today.plusDays(2));
      nonExistentBooking.setEndDate(today.plusDays(4));
      nonExistentBooking.setStatus(BookingStatus.CONFIRMED);
  
      when(bookingRepository.save(any(Booking.class))).thenThrow(new ResourceNotFoundException("Booking not found"));
  
      // Act & Assert
      ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
          bookingService.updateBooking(nonExistentBooking);
      });
  
      assertEquals("Booking not found", exception.getMessage());
      verify(bookingRepository).save(nonExistentBooking);
  }
  
  @Test
public void testUpdateBooking_shouldUpdateBookingDetailsSuccessfully() {
    // Arrange
    Booking updatedBooking = new Booking();
    updatedBooking.setId(1L);
    updatedBooking.setUser(user);
    updatedBooking.setResource(resource);
    updatedBooking.setStartDate(today.plusDays(5));
    updatedBooking.setEndDate(today.plusDays(7));
    updatedBooking.setStatus(BookingStatus.PENDING);

    when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);

    // Act
    Booking result = bookingService.updateBooking(updatedBooking);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(today.plusDays(5), result.getStartDate());
    assertEquals(today.plusDays(7), result.getEndDate());
    assertEquals(BookingStatus.PENDING, result.getStatus());
    verify(bookingRepository).save(updatedBooking);
}

@Test
public void testUpdateBooking_shouldThrowResourceNotFoundExceptionForNonExistentBooking() {
    // Arrange
    when(bookingRepository.save(any(Booking.class))).thenThrow(new ResourceNotFoundException("Booking not found"));

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
        bookingService.updateBooking(booking);
    });
    assertEquals("Booking not found", exception.getMessage());
}

@Test
public void testCreateBooking_shouldHandleEdgeCaseDates() {
    // Arrange
    LocalDate startDate = today.withDayOfMonth(1);
    LocalDate endDate = today.withDayOfMonth(today.lengthOfMonth());
    booking.setStartDate(startDate);
    booking.setEndDate(endDate);

    when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Booking result = bookingService.createBooking(booking);

    // Assert
    assertEquals(startDate, result.getStartDate());
    assertEquals(endDate, result.getEndDate());
    verify(bookingRepository).save(booking);
}

@Test
public void testCreateBooking_shouldAllowOverlappingBookingsForDifferentResources() {
    // Arrange
    Resource resource2 = new TestResource();
    resource2.setId(2L);
    resource2.setCapacity(5);

    Booking booking2 = new Booking();
    booking2.setId(2L);
    booking2.setUser(user);
    booking2.setResource(resource2);
    booking2.setStartDate(booking.getStartDate());
    booking2.setEndDate(booking.getEndDate());

    when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Booking result1 = bookingService.createBooking(booking);
    Booking result2 = bookingService.createBooking(booking2);

    // Assert
    assertNotNull(result1);
    assertNotNull(result2);
    assertNotEquals(result1.getResource(), result2.getResource());
    verify(bookingRepository, times(2)).save(any(Booking.class));
}

  public void setBookingStatus_shouldUpdateBookingStatus() {
    // Arrange
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    // Act
    bookingService.setBookingStatus(1L, BookingStatus.CONFIRMED, 1L, true);

    // Assert
    assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    verify(bookingRepository).save(booking);

  }

  @Test
  public void testSetBookingStatus_shouldThrowResourceNotFoundExceptionWhenBookingNotFound() {
    // Arrange
    when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      bookingService.setBookingStatus(1L, BookingStatus.CONFIRMED, 1L, true);
    });

    assertEquals("Booking not found with ID: 1", exception.getMessage());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  public void testSetBookingStatus_shouldNotPublishEventWhenCanceledByNonAdmin() {
    // Arrange
    ApplicationEventPublisher mockEventPublisher = mock(ApplicationEventPublisher.class);
    bookingService = new BookingService(bookingRepository, resourceServiceFactory, mockEventPublisher, timeProvider);

    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    // Act
    bookingService.setBookingStatus(1L, BookingStatus.CANCELED, 1L, false);

    // Assert
    assertEquals(BookingStatus.CANCELED, booking.getStatus());
    verify(bookingRepository).save(booking);
    verify(mockEventPublisher, never()).publishEvent(any(CancelNotificationEvent.class));
  }

  @Test
  public void testSetHandoverName_shouldUpdateHandoverName() {
    // Arrange
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    // Act
    bookingService.setHandoverName(1L, "Handover Name");

    // Assert
    assertEquals("Handover Name", booking.getHandoverName());
    assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    verify(bookingRepository).save(booking);
  }

  @Test
  public void testSetHandoverName_shouldThrowExceptionForInvalidName() {
    // Arrange
    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      bookingService.setHandoverName(1L, "");
    });

    assertEquals("Handover name cannot be null or empty.", exception.getMessage());
    verify(bookingRepository, never()).save(any());
  }

}
