package com.auu_sw3_6.Himmerland_booking_software.api.controller.testSecurityHelpers;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.auu_sw3_6.Himmerland_booking_software.service.TimeProvider;

@TestConfiguration
public class TestTimeProviderConfig {
  @Bean
  public TimeProvider timeProvider() {
    return new TimeProvider() {
      @Override
      public LocalTime getNow() {
        return LocalTime.of(10, 5);
      }

      @Override
      public LocalDate getToday() {
        return LocalDate.of(2024, 11, 4);
      }

    };
  }
}
