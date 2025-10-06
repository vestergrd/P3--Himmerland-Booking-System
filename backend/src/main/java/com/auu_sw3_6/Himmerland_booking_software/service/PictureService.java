package com.auu_sw3_6.Himmerland_booking_software.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PictureService {

  // Hardcoded for now, should properly be changed to a more dynamic solution,
  // maybe
  protected static String PROFILE_PICTURE_DIRECTORY = Paths
      .get("src", "main", "resources", "database", "img", "profilePictures").toAbsolutePath().toString();

  protected static String RESOURCE_PICTURE_DIRECTORY = Paths
      .get("src", "main", "resources", "database", "img", "resourcePictures").toAbsolutePath().toString();

  public PictureService() {
  }

  public String savePicture(MultipartFile picture, boolean isProfilePicture) {
    String directoryPath = isProfilePicture ? PROFILE_PICTURE_DIRECTORY : RESOURCE_PICTURE_DIRECTORY;

    validatePicture(picture);

    try {
      File directory = new File(directoryPath);
      if (!directory.exists()) {
        directory.mkdirs();
      }

      String originalFileName = picture.getOriginalFilename();
      String extension = "";
      if (originalFileName != null) {
        int index = originalFileName.lastIndexOf('.');
        if (index > 0) {
          extension = originalFileName.substring(index);
        }
      }

      String uniqueFileName = UUID.randomUUID().toString() + extension;
      Path imagePath = Paths.get(directoryPath, uniqueFileName);
      picture.transferTo(imagePath.toFile());

      return uniqueFileName;
    } catch (IOException e) {
      throw new RuntimeException("Failed to save picture", e);
    }
  }

  public void validatePicture(MultipartFile picture) {
    try {
        // Check file type
        String contentType = picture.getContentType();
        if (contentType == null || 
           (!contentType.equals("image/png") && !contentType.equals("image/jpeg"))) {
            throw new IllegalArgumentException("Invalid file type. Only PNG and JPG are allowed.");
        }

        // Check dimensions
        BufferedImage bufferedImage = ImageIO.read(picture.getInputStream());
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Invalid image file.");
        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        if (width != 300 || height != 300) {
            throw new IllegalArgumentException("Image dimensions must be 300x300.");
        }
    } catch (IOException e) {
        throw new RuntimeException("Failed to validate picture", e);
    }
}

  public Optional<byte[]> readPicture(String fileName, boolean isProfilePicture) {
    String directoryPath = isProfilePicture ? PROFILE_PICTURE_DIRECTORY : RESOURCE_PICTURE_DIRECTORY;
    if (fileName != null) {
      File file = new File(directoryPath, fileName);
      if (!file.exists()) {
        return Optional.empty();
      }
      try {
        byte[] pictureBytes = Files.readAllBytes(file.toPath());
        return Optional.of(pictureBytes);
      } catch (IOException e) {
        throw new RuntimeException("Failed to read picture", e);
      }
    }
    return Optional.empty();
  }
  
  public void setProfilePictureDirectory(String profilePictureDirectory) {
    PictureService.PROFILE_PICTURE_DIRECTORY = profilePictureDirectory;
  }

  public String getProfilePictureDirectory() {
    return PROFILE_PICTURE_DIRECTORY;
  }
}
