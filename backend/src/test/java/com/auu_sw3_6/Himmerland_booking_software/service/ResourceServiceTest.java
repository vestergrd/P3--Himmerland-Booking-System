package com.auu_sw3_6.Himmerland_booking_software.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Resource;
import com.auu_sw3_6.Himmerland_booking_software.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {

    @Mock
    private JpaRepository<Resource, Long> repository;

    @Mock
    private PictureService pictureService;

    @Mock
    private MultipartFile resourcePicture;

    @InjectMocks
    private TestResourceService resourceService;

    private ConcreteResource resource;

    private static class ConcreteResource extends Resource {
        // Implement any abstract methods if necessary
    }

    private static class TestResourceService extends ResourceService<Resource> {
        public TestResourceService(JpaRepository<Resource, Long> repository, PictureService pictureService) {
            super(repository, pictureService);
        }
    }

    @BeforeEach
    public void setUp() {
        resourceService = new TestResourceService(repository, pictureService);

        resource = new ConcreteResource();
        resource.setId(1L);
        resource.setName("TestResource");
        resource.setResourcePictureFileName("nonexistent.jpg");
    }

    @Test
    public void testCreateResource_ShouldSaveResourceWithPicture() {
        // Arrange
        when(resourcePicture.isEmpty()).thenReturn(false);

        String uniqueFileName = UUID.randomUUID().toString() + ".jpg";
        when(pictureService.savePicture(any(MultipartFile.class), any(Boolean.class)))
                .thenReturn(uniqueFileName);

        when(repository.save(any(Resource.class))).thenReturn(resource);

        // Act
        Resource createdResource = resourceService.createResource(resource, resourcePicture);

        // Assert
        assertNotNull(createdResource.getResourcePictureFileName(),
                "Resource picture should be saved with a unique filename.");
        assertEquals(uniqueFileName, createdResource.getResourcePictureFileName(),
                "The saved filename should match the generated unique filename.");
    }

    @Test
    public void testCreateResource_ShouldThrowExceptionWhenSavingPictureFails() {
        // Arrange
        when(resourcePicture.isEmpty()).thenReturn(false);
        when(pictureService.savePicture(any(MultipartFile.class), any(Boolean.class)))
                .thenThrow(new RuntimeException("Disk error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            resourceService.createResource(resource, resourcePicture);
        });
        assertEquals("Disk error", exception.getMessage(), "Exception message should match.");
    }

    @Test
    public void testGetAllResources_ShouldReturnAllResources() {
        // Arrange
        List<Resource> resources = List.of(new ConcreteResource(), new ConcreteResource());
        when(repository.findAll()).thenReturn(resources);

        // Act
        List<Resource> result = resourceService.getAllResources();

        // Assert
        assertEquals(2, result.size(), "Should return all resources from the database.");
    }

    @Test
    public void testGetResourceById_ShouldReturnResourceIfFound() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(resource));

        // Act
        Optional<Resource> result = resourceService.getResourceById(1L);

        // Assert
        assertTrue(result.isPresent(), "Should return the resource if found.");
        assertEquals("TestResource", result.get().getName(), "Resource name should match.");
    }

    @Test
    public void testGetResourcePictureByResourceId_ShouldReturnEmptyIfPictureNotFound() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(resource));
        when(pictureService.readPicture(eq("nonexistent.jpg"), eq(false))).thenReturn(Optional.empty());

        // Act
        Optional<byte[]> result = resourceService.getresourcePicturesByResourceId(1L);

        // Assert
        assertFalse(result.isPresent(), "Should return an empty Optional if the picture is not found.");
    }

    @Test
    public void testUpdateResource_ShouldUpdateResourceWithNewPicture() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(resource));
        when(resourcePicture.isEmpty()).thenReturn(false);

        String uniqueFileName = UUID.randomUUID().toString() + ".jpg";
        when(pictureService.savePicture(any(MultipartFile.class), any(Boolean.class)))
                .thenReturn(uniqueFileName);

        when(repository.save(any(Resource.class))).thenReturn(resource);

        resource.setName("UpdatedName");

        // Act
        Resource updatedResource = resourceService.updateResource(resource, resourcePicture);

        // Assert
        assertEquals("UpdatedName", updatedResource.getName(), "Resource name should be updated.");
        assertEquals(uniqueFileName, updatedResource.getResourcePictureFileName(),
                "Resource picture filename should be updated.");
    }

    @Test
    public void testSoftDeleteResource_ShouldSetStatusToDeleted() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(resource));
        when(repository.save(any(Resource.class))).thenReturn(resource);

        // Act
        boolean result = resourceService.softDeleteResource(1L);

        // Assert
        assertTrue(result, "Soft delete should return true.");
        assertEquals("deleted", resource.getStatus(), "Resource status should be set to 'deleted'.");
    }

    @Test
    public void testSoftDeleteResource_ShouldThrowExceptionIfNotFound() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> resourceService.softDeleteResource(1L),
                "Should throw ResourceNotFoundException if resource is not found.");
    }

    @Test
    public void testGetResourceByResourceName_ShouldReturnResourceIfFound() {
        // Arrange
        String resourceName = "TestResource";
        when(repository.findAll()).thenReturn(List.of(resource));

        // Act
        Optional<Resource> result = resourceService.getResourceByResourceName(resourceName);

        // Assert
        assertTrue(result.isPresent(), "Should return the resource if found");
        assertEquals(resourceName, result.get().getName(), "Resource name should match");
    }

    @Test
    public void testGetResourceByResourceName_ShouldReturnEmptyIfNotFound() {
        // Arrange
        String resourceName = "NonexistentResource";
        when(repository.findAll()).thenReturn(List.of(resource));

        // Act
        Optional<Resource> result = resourceService.getResourceByResourceName(resourceName);

        // Assert
        assertFalse(result.isPresent(), "Should return an empty Optional if the resource is not found.");
    }
}
