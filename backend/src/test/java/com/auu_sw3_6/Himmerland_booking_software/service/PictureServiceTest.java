package com.auu_sw3_6.Himmerland_booking_software.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;

import org.mockito.Mock;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class PictureServiceTest {

  private PictureService pictureService;

  @Mock
  private MultipartFile profilePictureTest;

  @BeforeEach
  public void setUp() {
    pictureService = new PictureService();
  }

  @Test
  public void testSaveProfilePicture_shouldSavePictureAndReturnUniqueFileName() throws Exception {
    // Arrange
    String originalFileName = "testProfile.jpg";
    MockMultipartFile profilePicture = new MockMultipartFile("file", originalFileName, "image/jpeg",
        new byte[] { 1, 2, 3, 4 });
    
    PictureService spyPictureService = org.mockito.Mockito.spy(pictureService);
    doNothing().when(spyPictureService).validatePicture(profilePicture);

    // Act
    String savedFileName = spyPictureService.savePicture(profilePicture, true); // Pass 'profilePicture' instead of 'profilePictureTest'
  
    // Assert
    assertNotNull(savedFileName);
    assertTrue(savedFileName.endsWith(".jpg") || savedFileName.endsWith(".png"));
  
    // Check if the file was saved
    File savedFile = new File(Paths.get(pictureService.getProfilePictureDirectory(), savedFileName).toString());
    assertTrue(savedFile.exists());
  
    // Cleanup
    Files.delete(savedFile.toPath());
  }
  
  @Test
  public void testReadProfilePicture_shouldReturnBytesIfFileExists() throws IOException {
    // Arrange
    String fileName = "existingPicture.jpg";
    File tempFile = new File(pictureService.getProfilePictureDirectory(), fileName);
    Files.write(tempFile.toPath(), new byte[] { 1, 2, 3, 4 });

    // Act
    Optional<byte[]> pictureBytes = pictureService.readPicture(fileName, true);

    // Assert
    assertTrue(pictureBytes.isPresent());
    assertEquals(4, pictureBytes.get().length);

    // Cleanup
    Files.delete(tempFile.toPath());
  }

  @Test
  public void testReadProfilePicture_shouldReturnEmptyOptionalIfFileDoesNotExist() {
    // Arrange
    String nonExistentFileName = "nonExistentPicture.jpg";

    // Act
    Optional<byte[]> pictureBytes = pictureService.readPicture(nonExistentFileName, true);

    // Assert
    assertFalse(pictureBytes.isPresent());
  }

  @Test
  public void testSaveProfilePicture_shouldThrowExceptionWhenIOExceptionOccurs() throws Exception {
    // Arrange
    lenient().when(profilePictureTest.getOriginalFilename()).thenReturn("testProfile.jpg");
    lenient().when(profilePictureTest.isEmpty()).thenReturn(false);
    PictureService spyPictureService = org.mockito.Mockito.spy(pictureService);
    doNothing().when(spyPictureService).validatePicture(profilePictureTest);
    doThrow(new IOException("Disk error")).when(profilePictureTest).transferTo(any(File.class));
  
    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      spyPictureService.savePicture(profilePictureTest, true);
    });
    assertEquals("Failed to save picture", exception.getMessage());
  }
  
  @Test
  public void testSaveProfilePicture_shouldThrowExceptionForUnsupportedFileType() {
      // Arrange
      MockMultipartFile unsupportedFile = new MockMultipartFile("file", "unsupportedFile.txt", "text/plain", new byte[] {1, 2, 3, 4});
      
      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
          pictureService.savePicture(unsupportedFile, true);
      });
      assertEquals("Invalid file type. Only PNG and JPG are allowed.", exception.getMessage());
  }
 
}
