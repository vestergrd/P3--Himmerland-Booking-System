package com.auu_sw3_6.Himmerland_booking_software.api.controller.testSecurityHelpers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Admin;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Tenant;
import com.auu_sw3_6.Himmerland_booking_software.api.model.User;
import com.auu_sw3_6.Himmerland_booking_software.config.security.CustomUserDetails;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

public class SecurityContextHelper {

  public static void setSecurityContext(String role) {
    User user;
    if (role.toUpperCase().equals("ADMIN")) {
      user = new Admin();
    } else {
      user = new Tenant();
    }
    
    user.setId(1L);
    user.setUsername("testUser");
    user.setPassword("rawPassword123");
    user.setEmail("testEmail@mail.com");
    user.setMobileNumber("88888888");
    user.setName("testName");
    user.setHouseAddress("testAddress");
    user.setProfilePictureFileName("testPicture");

    setSecurityContext(user, role);
  }

  public static void setSecurityContext(User user, String role) {

    role = "ROLE_" + role.toUpperCase();
    // Create a UserDetails object with the provided user and role
    CustomUserDetails userDetails = new CustomUserDetails(user,
        Collections.singletonList(new SimpleGrantedAuthority(role)));

    // Create an authentication token with the user details
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());

    // Set the authentication in the SecurityContextHolder
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);
  }

  public static void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }
}
