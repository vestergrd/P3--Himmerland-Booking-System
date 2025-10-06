package com.auu_sw3_6.Himmerland_booking_software.api.model;

import jakarta.persistence.Entity;

@Entity
public class Tenant extends User {

  public Tenant() {
    super();
  }

  public Tenant(Long id, String username, String email, String phoneNumber, String fullName, String password,
      String profilePicture, String address) {
    super(id, username, email, phoneNumber, fullName, password, profilePicture, address);
  }

}
