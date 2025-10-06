package com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TimeRange {
  EARLY("7:00-7:30"),
  LATE("11:00-12:00");

  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");
  private final String timeSlot;

  TimeRange(String timeSlot) {
    this.timeSlot = timeSlot;
  }

  public String getTimeSlot() {
    return this.timeSlot;
  }

  @JsonCreator
  public static TimeRange fromString(String timeSlot) {
    for (TimeRange range : TimeRange.values()) {
      if (range.getTimeSlot().equals(timeSlot)) {
        return range;
      }
    }
    throw new IllegalArgumentException("Unknown time slot: " + timeSlot);
  }

  @JsonValue
  public String toValue() {
    return this.timeSlot;
  }

  public LocalTime getEndTime() {
    return LocalTime.parse(timeSlot.split("-")[1], TIME_FORMATTER);
  }

  public LocalTime getStartTime() {
    return LocalTime.parse(timeSlot.split("-")[0], TIME_FORMATTER);
}

}
