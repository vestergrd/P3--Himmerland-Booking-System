package com.auu_sw3_6.Himmerland_booking_software.api.model;

import java.time.LocalDate;

import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.BookingStatus;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.TimeRange;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "resource_id", nullable = false)
  private Resource resource;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  private LocalDate startDate;
  private LocalDate endDate;

  private TimeRange pickupTime;
  private TimeRange dropoffTime;

  @Enumerated(EnumType.STRING)
  private BookingStatus status;

  private String receiverName;

  private String handoverName;

  public Booking() {
  }

  public Booking(Resource resource, User user, LocalDate startDate, LocalDate endDate,
  TimeRange pickupTime, TimeRange dropoffTime, BookingStatus status, String receiverName, String handoverName) {
    this.resource = resource;
    this.user = user;
    this.startDate = startDate;
    this.endDate = endDate;
    this.pickupTime = pickupTime;
    this.dropoffTime = dropoffTime;
    this.status = status;
    this.receiverName = receiverName;
    this.handoverName = handoverName;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public TimeRange getPickupTime() {
    return pickupTime;
  }

  public void setPickupTime(TimeRange pickupTime) {
    this.pickupTime = pickupTime;
  }

  public TimeRange getDropoffTime() {
    return dropoffTime;
  }

  public void setDropoffTime(TimeRange dropoffTime) {
    this.dropoffTime = dropoffTime;
  }

  public BookingStatus getStatus() {
    return status;
  }

  public void setStatus(BookingStatus status) {
    this.status = status;
  }

  public String getReceiverName(){
    return receiverName;
  }

  public void setReceiverName(String receiverName){
    this.receiverName = receiverName;
  }

  public String getHandoverName(){
    return handoverName;
  }

  public void setHandoverName(String handoverName){
    this.handoverName = handoverName;
  }

}
