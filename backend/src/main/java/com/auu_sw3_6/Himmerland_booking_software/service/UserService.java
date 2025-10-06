package com.auu_sw3_6.Himmerland_booking_software.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Booking;
import com.auu_sw3_6.Himmerland_booking_software.api.model.BookingDetails;
import com.auu_sw3_6.Himmerland_booking_software.api.model.User;
import com.auu_sw3_6.Himmerland_booking_software.config.security.CustomUserDetails;
import com.auu_sw3_6.Himmerland_booking_software.exception.UserNotFoundException;

@Service
public abstract class UserService<T extends User> {

  private final JpaRepository<T, Long> repository;
  private final PictureService pictureService;
  private final PasswordEncoder passwordEncoder;
  protected final BookingService bookingService;

  @Autowired
  public UserService(JpaRepository<T, Long> repository, PictureService pictureService,
      PasswordEncoder passwordEncoder, BookingService bookingService) {
    this.repository = repository;
    this.pictureService = pictureService;
    this.passwordEncoder = passwordEncoder;
    this.bookingService = bookingService;
  }

  public User updateUser(Long id, User updatedUser) {
    return repository.findById(id)
        .map(user -> {
          user.setName(updatedUser.getName());
          user.setEmail(updatedUser.getEmail());
          user.setMobileNumber(updatedUser.getMobileNumber());
          user.setUsername(updatedUser.getUsername());
          user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
          user.setProfilePictureFileName(updatedUser.getProfilePictureFileName());
          user.setHouseAddress(updatedUser.getHouseAddress());
          return repository.save(user);
        })
        .orElseThrow(() -> new UserNotFoundException("User not found"));
  }

  public T createUser(T user, MultipartFile profilePicture) {
    if (profilePicture != null && !profilePicture.isEmpty()) {
      String uniqueFileName = pictureService.savePicture(profilePicture, true);
      user.setProfilePictureFileName(uniqueFileName);
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return repository.save(user);
  }

  public List<T> getAllUsers() {
    return repository.findAll();
  }

  public Optional<T> getUserById(Long id) {
    return repository.findById(id);
  }

  public Optional<byte[]> getProfilePictureByUserId(long userId) {
    Optional<T> userOptional = repository.findById(userId);
    return userOptional.flatMap(user -> pictureService.readPicture(user.getProfilePictureFileName(), true));
  }

  public void deleteUser(Long id) {
    repository.deleteById(id);
  }

  public Optional<byte[]> getProfilePictureByUsername(String username) {
    Optional<T> user = getUserByUsername(username);
    return user.flatMap(u -> getProfilePictureByUserId(u.getId()));
  }

  public Optional<T> getUserByUsername(String username) {
    return repository.findAll().stream()
        .filter(u -> u.getUsername().equals(username))
        .findFirst();
  }

  public User getAuthenticatedUser() throws UserNotFoundException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new UserNotFoundException("User not authenticated");
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    User user = userDetails.toUser();

    if (user == null) {
      throw new UserNotFoundException("User not found");
    }

    return user;
  }

  public Boolean currentUserIsAdmin() {
    return getUserRole().equals("ADMIN");
  }

  private String getUserRole() {
    return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
        .findFirst()
        .map(GrantedAuthority::getAuthority)
        .orElse(null);
  }

  public Booking createBooking(BookingDetails details) {
    User user = getAuthenticatedUser();
    return bookingService.bookResource(user, details);
  }

  public Booking editBooking(long bookingId, BookingDetails details) {
    User user = getAuthenticatedUser();
    return bookingService.editBooking(bookingId, details, user);
  }

  public Boolean setUserProfilePicture(User user, MultipartFile profilePicture) {
    String uniqueFileName = pictureService.savePicture(profilePicture, true);
    user.setProfilePictureFileName(uniqueFileName);
    return true;
  }

  public boolean isValidPassword(String password) {

    if (password == null || password.length() < 8) {
      return false;
    }

    boolean hasUpper = false, hasLower = false, hasDigit = false;

    for (char c : password.toCharArray()) {
      if (Character.isUpperCase(c)) {
        hasUpper = true;
      } else if (Character.isLowerCase(c)) {
        hasLower = true;
      } else if (Character.isDigit(c)) {
        hasDigit = true;
      }
    }
    return hasUpper && hasLower && hasDigit;
  }



  public T updateUser(T user, MultipartFile pictureFile) {
    Optional<T> existingAdminOptional = repository.findById(user.getId());

    if (existingAdminOptional.isPresent()) {
      T existingAdmin = existingAdminOptional.get();

      if (pictureFile != null && !pictureFile.isEmpty()) {
        setUserProfilePicture(user, pictureFile);
      }

      if (user.getPassword() != null && !user.getPassword().isEmpty()) {

        if (isValidPassword(user.getPassword())) {
          user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
          throw new IllegalArgumentException(
              "Password is invalid, needs to contain atleast 8 chars, 1 digit, 1 uppercase, and 1 lowercase");
        }
      } else {
        // If the password is null or empty, retain the existing password
        user.setPassword(existingAdmin.getPassword());
      }
      return repository.save(user);
    } else {
      throw new IllegalArgumentException("User with ID " + user.getId() + " not found");
    }
  }














}
