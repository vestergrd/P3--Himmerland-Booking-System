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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Tool;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.ToolRepository;

@ExtendWith(MockitoExtension.class)
public class ToolServiceTest {

    @Mock
    private ToolRepository toolRepository;

    @Mock
    private PictureService pictureService;  // Mock the PictureService

    @Mock
    private MultipartFile resourcePictures;

    @InjectMocks
    private ToolService toolService;

    private Tool tool;

    @BeforeEach
    public void setUp() {
        tool = new Tool();
        tool.setId(1L);
        tool.setName("Test Tool");
    }

    @Test
    public void testCreateTool_shouldSaveToolAndReturnToolObject() {
        // Arrange
        when(toolRepository.save(any(Tool.class))).thenReturn(tool);
        when(pictureService.savePicture(any(MultipartFile.class), any(Boolean.class))).thenReturn("savedPicture.jpg");  // Mock savePicture

        // Act
        Tool createdTool = toolService.createTool(tool, resourcePictures);

        // Assert
        assertNotNull(createdTool);
        assertEquals("Test Tool", createdTool.getName());
        verify(toolRepository).save(tool);
        verify(pictureService).savePicture(any(MultipartFile.class), any(Boolean.class));  // Verify that savePicture was called
    }

    @Test
    public void testCreateTool_shouldThrowExceptionWhenRepositoryFails() {
        // Arrange
        when(toolRepository.save(any(Tool.class))).thenThrow(new RuntimeException("Database error"));
        when(pictureService.savePicture(any(MultipartFile.class), any(Boolean.class))).thenReturn("savedPicture.jpg");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            toolService.createTool(tool, resourcePictures);
        });
        assertEquals("Database error", exception.getMessage());
    }

}
 