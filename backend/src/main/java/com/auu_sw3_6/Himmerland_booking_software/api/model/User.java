package com.auu_sw3_6.Himmerland_booking_software.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Unique identifier of the user", accessMode = Schema.AccessMode.READ_ONLY)
  private long id;

  @NotNull(message = "Name cannot be null")
  @Size(min = 3, message = "Name should have at least 3 characters")
  @Schema(description = "The user's name")
  private String name;

  @NotNull(message = "Email cannot be null")
  @Email(message = "Email should be valid")
  @Schema(description = "The user's email address")
  private String email;

  @Pattern(regexp = "\\+?[0-9. ()-]{7,25}", message = "Mobile number is invalid")
  @Schema(description = "The user's mobile phone number")
  private String mobileNumber;

  @NotNull(message = "Username cannot be null")
  @Size(min = 3, message = "Username should have at least 3 characters")
  @Schema(description = "The user's username")
  private String username;

  @NotNull(message = "Password cannot be null")
  @Size(min = 8, message = "Password should have at least 8 characters")
  @JsonIgnore(false)
  @Schema(description = "The user's password (encrypted)", accessMode = Schema.AccessMode.WRITE_ONLY)
  private String password;

  @Schema(description = "File name of the user's profile picture", accessMode = Schema.AccessMode.READ_ONLY)
  private String profilePictureFileName;

  @Schema(description = "The user's house address")
  private String houseAddress;

  public User() {
  }

  public User(long id, String name, String email, String mobileNumber, String username, String password,
      String profilePictureFileName, String houseAddress) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.mobileNumber = mobileNumber;
    this.username = username;
    this.password = password;
    this.profilePictureFileName = profilePictureFileName;
    this.houseAddress = houseAddress;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getMobileNumber() {
    return mobileNumber;
  }

  public void setMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getProfilePictureFileName() {
    return profilePictureFileName;
  }

  public void setProfilePictureFileName(String profilePictureFileName) {
    this.profilePictureFileName = profilePictureFileName;
  }

  public String getHouseAddress() {
    return houseAddress;
  }

  public void setHouseAddress(String houseAddress) {
    this.houseAddress = houseAddress;
  }

}
