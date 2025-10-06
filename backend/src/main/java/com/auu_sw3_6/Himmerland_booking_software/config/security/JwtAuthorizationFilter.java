package com.auu_sw3_6.Himmerland_booking_software.config.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auu_sw3_6.Himmerland_booking_software.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

  @Autowired
  private final JwtUtil jwtUtil;

  @Autowired
  private final CustomUserDetailsService userDetailsService;

  public JwtAuthorizationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
      throws ServletException, IOException {

    String token = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("jwt".equals(cookie.getName())) {
          token = cookie.getValue();
          break;
        }
      }
    }

    if (token == null) {
      chain.doFilter(request, response);
      return;
    }
    String username = jwtUtil.extractUsername(token);

    // Log the extracted username
    logger.info("JWT parsed, extracted username: {}", username);

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      // Validate the token
      if (jwtUtil.validateToken(token, username)) {
        // Load user details using CustomUserDetailsService
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Log the roles of the user
        logger.info("User roles retrieved for username {}: {}", username, userDetails.getAuthorities());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
      } else {
        // Log when the token is invalid
        logger.warn("Invalid JWT for username: {}", username);
      }
    }

    chain.doFilter(request, response);
  }
}
