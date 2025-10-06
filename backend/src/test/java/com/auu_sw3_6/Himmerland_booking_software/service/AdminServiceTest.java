package com.auu_sw3_6.Himmerland_booking_software.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Admin;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.AdminRepository;
import com.auu_sw3_6.Himmerland_booking_software.exception.CaretakerNameConflictException;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

  @Mock
  private AdminRepository adminRepository;

  @Mock
  private PictureService pictureService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private MultipartFile profileImage;

  @InjectMocks
  private AdminService adminService;

  private Admin admin;

  private final boolean isProfilePicture = true;

  @BeforeEach
  public void setUp() {
    admin = new Admin();
    admin.setId(1L);
    admin.setUsername("adminUser");
    admin.setPassword("Password123");
    admin.setCaretakerNames(new ArrayList<>());
  }

  @Test
  public void testCreateAdmin_InteractsWithDependencies() throws Exception {
    // Arrange
    when(pictureService.savePicture(profileImage, isProfilePicture)).thenReturn("profileImage.jpg");
    when(passwordEncoder.encode("Password123")).thenReturn("encryptedPassword123");
    when(adminRepository.save(any(Admin.class))).thenReturn(admin);

    // Act
    adminService.createAdmin(admin, profileImage);

    // Assert
    verify(adminRepository).save(admin);
    verify(pictureService).savePicture(profileImage, isProfilePicture);
    verify(passwordEncoder).encode("Password123");
  }

  @Test
  public void testCreateAdmin_ReturnsAdminWithCorrectDetails() throws Exception {
    // Arrange
    when(pictureService.savePicture(profileImage, isProfilePicture)).thenReturn("profileImage.jpg");
    when(passwordEncoder.encode("Password123")).thenReturn("encryptedPassword123");
    when(adminRepository.save(any(Admin.class))).thenReturn(admin);

    // Act
    Admin createdAdmin = adminService.createAdmin(admin, profileImage);

    // Assert
    assertNotNull(createdAdmin, "The created admin should not be null");
    assertEquals("encryptedPassword123", createdAdmin.getPassword(), "The password should be encrypted");
    assertEquals("profileImage.jpg", createdAdmin.getProfilePictureFileName(),
        "The profile picture file name should match");
  }

  @Test
  public void testCreateAdmin_ThrowsExceptionForInvalidProfilePicture() {
    // Arrange
    when(pictureService.savePicture(profileImage, isProfilePicture))
        .thenThrow(new IllegalArgumentException("Unsupported file type"));

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      adminService.createAdmin(admin, profileImage);
    });
    assertEquals("Unsupported file type", exception.getMessage());
  }

  @Test // ALLE TESTS MED UPDATEUSER ER FCKED PGA isValidPassword -> fix
  public void testUpdateAdmin_InteractsWithDependencies() {
    // Arrange
    when(adminRepository.findById(1L)).thenReturn(java.util.Optional.of(admin));
    when(pictureService.savePicture(profileImage, isProfilePicture)).thenReturn("profileImage.jpg");
    when(passwordEncoder.encode("Password123")).thenReturn("encryptedPassword123");
    when(adminRepository.save(any(Admin.class))).thenReturn(admin);

    // Act
    adminService.updateUser(admin, profileImage);

    // Assert
    verify(adminRepository).findById(1L);
    verify(adminRepository).save(admin);
    verify(pictureService).savePicture(profileImage, isProfilePicture);
    verify(passwordEncoder).encode("Password123");
  }

  @Test // Update user happens in UserService, not AdminService
  public void testUpdateAdmin_ThrowsExceptionWhenAdminNotFound() {
    // Arrange
    when(adminRepository.findById(1L)).thenReturn(java.util.Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
      adminService.updateUser(admin, profileImage);
    });
    assertEquals("User with ID 1 not found", exception.getMessage());
    // User with ID 1 not found / Admin with ID 1 not found
    // It can only be User since AdminService extends UserService
  }

  @Test
  public void testUpdateAdmin_RetainsExistingPasswordWhenNewPasswordIsNull() {
    // Arrange
    admin.setPassword(null);
    Admin existingAdmin = new Admin();
    existingAdmin.setId(1L);
    existingAdmin.setPassword("existingPassword");
    when(adminRepository.findById(1L)).thenReturn(java.util.Optional.of(
        existingAdmin));
    when(adminRepository.save(any(Admin.class))).thenReturn(admin);

    // Act
    Admin updatedAdmin = adminService.updateUser(admin, profileImage);

    // Assert
    assertEquals("existingPassword", updatedAdmin.getPassword());
    verify(adminRepository).findById(1L);
    verify(adminRepository).save(admin);
  }

  @Test
  public void testUpdateAdmin_ReturnsUpdatedAdminWithCorrectDetails() {
    // Arrange
    when(adminRepository.findById(1L)).thenReturn(java.util.Optional.of(admin));
    when(pictureService.savePicture(profileImage,
        isProfilePicture)).thenReturn("profileImage.jpg");
    when(passwordEncoder.encode("Password123")).thenReturn("encryptedPassword123");
    when(adminRepository.save(any(Admin.class))).thenReturn(admin);

    // Act
    Admin updatedAdmin = adminService.updateUser(admin, profileImage);

    // Assert
    assertNotNull(updatedAdmin, "The updated admin should not be null");
    assertEquals("encryptedPassword123", updatedAdmin.getPassword(),
        "The password should be encrypted");
    assertEquals("profileImage.jpg", updatedAdmin.getProfilePictureFileName(),
        "The profile picture file name should match");
  }

    @Test
    public void testAddCaretakerName_AddsNewCaretakerName() {
      // Arrange
      String caretakerName = "John Doe";
      when(adminRepository.findById(1L)).thenReturn(java.util.Optional.of(admin));
      when(adminRepository.save(any(Admin.class))).thenReturn(admin);

      // Act
      adminService.addCaretakerName(caretakerName);

      // Assert
      assertNotNull(admin.getCaretakerNames());
      assertEquals(1, admin.getCaretakerNames().size());
      assertEquals("John Doe", admin.getCaretakerNames().get(0));
      verify(adminRepository).findById(1L);
      verify(adminRepository).save(admin);
    }

    @Test
    public void testAddCaretakerName_ThrowsExceptionForInvalidName() {
    // Arrange
    String caretakerName = "John Doe";
    admin.getCaretakerNames().add(caretakerName);
    when(adminRepository.findById(1L)).thenReturn(java.util.Optional.of(admin));

    // Act & Assert
    CaretakerNameConflictException exception = assertThrows(CaretakerNameConflictException.class, () -> {
        adminService.addCaretakerName(caretakerName);
    });
    assertEquals("Caretaker name: \"John Doe\" already exists", exception.getMessage());
    verify(adminRepository).findById(1L);
}

    

    @Test
      public void testRemoveCaretakerName_RemovesExistingCaretakerName() {
        // Arrange
        String caretakerName = "John Doe";
        admin.getCaretakerNames().add(caretakerName);
        when(adminRepository.findById(1L)).thenReturn(java.util.Optional.of(admin));
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        // Act
        adminService.removeCaretakerName(caretakerName);

        // Assert
        assertNotNull(admin.getCaretakerNames());
        assertEquals(0, admin.getCaretakerNames().size());
        verify(adminRepository).findById(1L);
        verify(adminRepository).save(admin);
      }

      @Test
      public void testRemoveCaretakerName_ThrowsExceptionForAdminNotFound() {
          // Arrange
          String caretakerName = "John Doe";
          when(adminRepository.findById(1L)).thenReturn(java.util.Optional.empty());
      
          // Act & Assert
          IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
              adminService.removeCaretakerName(caretakerName);
          });
      
          assertEquals("Admin not found with ID: 1", exception.getMessage());
          verify(adminRepository).findById(1L);
      }
      


}
