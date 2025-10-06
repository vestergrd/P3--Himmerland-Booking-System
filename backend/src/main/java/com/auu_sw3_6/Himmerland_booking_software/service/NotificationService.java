package com.auu_sw3_6.Himmerland_booking_software.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Booking;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Resource;
import com.auu_sw3_6.Himmerland_booking_software.api.model.User;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.TimeRange;
import com.auu_sw3_6.Himmerland_booking_software.service.event.CancelNotificationEvent;

@Service
public class NotificationService {

  private BookingService bookingService;

  @Autowired
  public NotificationService(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @EventListener
  public void handleBookingNotification(CancelNotificationEvent event) {
    Booking booking = event.getBooking();
    sendCancellationNotification(booking);
  }

  public void earlyMorningNotification() {
    System.out.println("Sending early morning notification");
    sendTimeBasedNotifications(TimeRange.EARLY);
    sendMissedNotifications();
  }

  public void lateMorningNotification() {
    System.out.println("Sending late morning notification");
    sendTimeBasedNotifications(TimeRange.LATE);
  }

  public void sendTimeBasedNotifications(TimeRange timeRange) {
    sendPickupNotifications(timeRange);
    sendDropoffNotifications(timeRange);
  }

  public void sendPickupNotifications(TimeRange timeRange) {
    List<Booking> pickups = bookingService.getAllUpcomingPickupsForToday(timeRange);

    for (Booking booking : pickups) {
      User user = booking.getUser();
      Resource resource = booking.getResource();
      EmailService.sendPickupNotification(user.getEmail(), user.getName(), resource.getName(), timeRange.getTimeSlot());
    }
  }

  public void sendDropoffNotifications(TimeRange timeRange) {
    List<Booking> dropoffs = bookingService.getAllUpcomingDropoffsForToday(timeRange);

    for (Booking booking : dropoffs) {
      User user = booking.getUser();
      Resource resource = booking.getResource();
      EmailService.sendDropoffNotification(user.getEmail(), user.getName(), resource.getName(),
          timeRange.getTimeSlot());
    }
  }

  public void sendMissedNotifications() {
    System.out.println("Sending missed notifications...");
    List<Booking> missed = bookingService.getAllLateBookings();

    for (Booking booking : missed) {
      User user = booking.getUser();
      Resource resource = booking.getResource();
      EmailService.sendMissedDropoffNotification(
          user.getEmail(),
          user.getName(),
          resource.getName(),
          booking.getDropoffTime().getTimeSlot(),
          booking.getEndDate());
    }
  }

  public void sendCancellationNotification(Booking booking) {
    System.out.println("Sending cancellation notification");

    User user = booking.getUser();
    Resource resource = booking.getResource();
    EmailService.sendCancelledBookingNotification(user.getEmail(), user.getName(), resource.getName(),
        booking.getStartDate(), booking.getEndDate());
  }
}
