package com.auu_sw3_6.Himmerland_booking_software.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Utility;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.UtilityRepository;

@ExtendWith(MockitoExtension.class)
public class UtilityServiceTest {

    @Mock
    private UtilityRepository utilityRepository;

    @Mock
    private MultipartFile resourcePictures;

    @Mock
    private PictureService pictureService;

    @InjectMocks
    private UtilityService utilityService;

    private Utility utility;

    @BeforeEach
    public void setUp() {
        utility = new Utility();
        utility.setId(1L);
        utility.setName("Test Utility");
    }

    @Test
    public void testCreateUtility_CallsSaveMethodOnRepository() {
        // Arrange
        when(utilityRepository.save(any(Utility.class))).thenReturn(utility);

        // Act
        utilityService.createUtility(utility, resourcePictures);

        // Assert
        verify(utilityRepository).save(utility);
    }

    @Test
    public void testCreateUtility_ReturnsNonNullUtility() {
        // Arrange
        when(utilityRepository.save(any(Utility.class))).thenReturn(utility);

        // Act
        Utility createdUtility = utilityService.createUtility(utility, resourcePictures);

        // Assert
        assertNotNull(createdUtility, "Utility should be created and not null");
    }

    @Test
    public void testCreateUtility_ReturnsUtilityWithCorrectName() {
        // Arrange
        when(utilityRepository.save(any(Utility.class))).thenReturn(utility);

        // Act
        Utility createdUtility = utilityService.createUtility(utility, resourcePictures);

        // Assert
        assertEquals("Test Utility", createdUtility.getName(), "Utility name should be 'Test Utility'");
    }
    
}
