package com.auu_sw3_6.Himmerland_booking_software.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Admin;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Booking;
import com.auu_sw3_6.Himmerland_booking_software.api.model.BookingDetails;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Tenant;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.AdminRepository;
import com.auu_sw3_6.Himmerland_booking_software.exception.AdminNotFouldException;
import com.auu_sw3_6.Himmerland_booking_software.exception.CaretakerNameConflictException;

@Service
public class AdminService extends UserService<Admin> {

  private static final Long ADMIN_ID = 1L;
  private final AdminRepository adminRepository;
  private final BookingService bookingService;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public AdminService(AdminRepository adminRepository, PictureService profilePictureService,
      PasswordEncoder passwordEncoder, BookingService bookingService) {
    super(adminRepository, profilePictureService, passwordEncoder, bookingService);
    this.adminRepository = adminRepository;
    this.bookingService = bookingService;
    this.passwordEncoder = passwordEncoder;
  }

  public Admin createAdmin(Admin admin, MultipartFile profilePicture) {
    return createUser(admin, profilePicture);
  }

  public void addCaretakerName(String caretakerName) {
    String cleanedName = caretakerName.trim().replaceAll("^\"|\"$", "");

    Admin admin = adminRepository.findById(ADMIN_ID)
        .orElseThrow(() -> new IllegalStateException("Admin not found with ID: " + ADMIN_ID));

    if (admin.getCaretakerNames().contains(cleanedName)) {
      throw new CaretakerNameConflictException("Caretaker name: \"" + cleanedName + "\" already exists");
    }

    admin.addCaretakerName(cleanedName);
    adminRepository.save(admin);
  }

  public List<String> getAllCaretakerNames() {
    Admin admin = adminRepository.findById(ADMIN_ID)
        .orElseThrow(() -> new IllegalStateException("Admin not found with ID: " + ADMIN_ID));

    if (admin.getCaretakerNames().isEmpty()) {
      throw new AdminNotFouldException("No caretaker names found");
    }
    return admin.getCaretakerNames();
  }

  public void removeCaretakerName(String caretakerName) {
    Admin admin = adminRepository.findById(ADMIN_ID)
        .orElseThrow(() -> new IllegalStateException("Admin not found with ID: " + ADMIN_ID));

    if (!admin.getCaretakerNames().contains(caretakerName)) {
      throw new AdminNotFouldException("Caretaker name: \"" + caretakerName + "\" not found");
    }
    String cleanedName = caretakerName.trim().replaceAll("^\"|\"$", "");
    admin.removeCaretakerName(cleanedName);
    adminRepository.save(admin);
  }

  public Booking bookResourceForTenant(Tenant tenant, BookingDetails details) {
    if (tenant == null) {
      throw new IllegalArgumentException("Tenant cannot be null");
    }
    if (details == null) {
      throw new IllegalArgumentException("Booking details cannot be null");
    }
    return bookingService.bookResource(tenant, details);
  }
}