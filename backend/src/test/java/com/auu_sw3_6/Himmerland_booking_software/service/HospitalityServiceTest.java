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
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Hospitality;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.HospitalityRepository;

@ExtendWith(MockitoExtension.class)
public class HospitalityServiceTest {

    @Mock
    private HospitalityRepository hospitalityRepository;

    @Mock
    private PictureService pictureService;

    @Mock
    private MultipartFile resourcePictures;

    @InjectMocks
    private HospitalityService hospitalityService;

    private Hospitality hospitality;

    @BeforeEach
    public void setUp() {
        hospitality = new Hospitality();
        hospitality.setId(1L);
        hospitality.setName("Test Guest House");
    }

    @Test
    public void testCreateHospitality_WithValidName_ShouldCreateHospitality() {
        // Arrange
        when(hospitalityRepository.save(any(Hospitality.class))).thenReturn(hospitality);
        when(pictureService.savePicture(any(MultipartFile.class), any(Boolean.class))).thenReturn("someFileName.jpg");
        
        // Act
        Hospitality createdHospitality = hospitalityService.createHospitality(hospitality, resourcePictures);

        // Assert
        assertNotNull(createdHospitality, "Hospitality should be created and not null");
        assertEquals("Test Guest House", createdHospitality.getName(), "The name of the Hospitality should be 'Test Guest House'");
    }

    @Test
    public void testCreateHospitality_WithNullName_ShouldThrowException() {
        // Arrange
        hospitality.setName(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hospitalityService.createHospitality(hospitality, resourcePictures);
        });
        assertEquals("Name cannot be null or empty.", exception.getMessage(), "Should throw exception for null name");
    }

    @Test
    public void testCreateHospitality_WithEmptyName_ShouldThrowException() {
        // Arrange
        hospitality.setName("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hospitalityService.createHospitality(hospitality, resourcePictures);
        });
        assertEquals("Name cannot be null or empty.", exception.getMessage(), "Should throw exception for empty name");
    }
}
