package com.auu_sw3_6.Himmerland_booking_software.api.controller.testSecurityHelpers;

import java.io.IOException;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.auu_sw3_6.Himmerland_booking_software.config.security.JwtAuthorizationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@TestConfiguration
public class NoOpJwtAuthorizationFilterConfig {

  @Bean
  @Primary
  public JwtAuthorizationFilter noOpJwtAuthorizationFilter() {
    System.out.println("Running this instead of JwtAuthorizationFilter");
    return new JwtAuthorizationFilter(null, null) {
      @Override
      protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
          throws IOException, ServletException {
            System.out.println("Running this instead of JwtAuthorizationFilter 2");

        chain.doFilter(request, response);
      }
    };
  }
}
