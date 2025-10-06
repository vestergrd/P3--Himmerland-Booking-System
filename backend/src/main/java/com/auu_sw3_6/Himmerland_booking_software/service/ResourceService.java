package com.auu_sw3_6.Himmerland_booking_software.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Resource;
import com.auu_sw3_6.Himmerland_booking_software.exception.ResourceNotFoundException;

public abstract class ResourceService<T extends Resource> {


  @Value("${resource.pictures.directory}")
  private String resourcePicturesDirectory;

  private final JpaRepository<T, Long> repository;
  private final PictureService pictureService;

  @Autowired
  public ResourceService(JpaRepository<T, Long> repository, PictureService pictureService) {
    this.repository = repository;
    this.pictureService = pictureService;
  }

  public T createResource(T resource, MultipartFile resourcePictures) {
    if (resourcePictures != null && !resourcePictures.isEmpty()) {
      String uniqueFileName = pictureService.savePicture(resourcePictures, false);
      resource.setResourcePictureFileName(uniqueFileName);
    }

    return repository.save(resource);
  }

  public List<T> getAllResources() {
    return repository.findAll();
  }

  public Optional<T> getResourceById(Long id) {
    return repository.findById(id);
  }


  public Optional<byte[]> getresourcePicturesByResourceId(long resourceId) {
    Optional<T> resourceOptional = repository.findById(resourceId);
    return resourceOptional.flatMap(user -> pictureService.readPicture(user.getResourcePictureFileName(), false));
  }

  public T updateResource(T updatedResource, MultipartFile pictureFile) {
    Optional<T> existingResourceOptional = repository.findById(updatedResource.getId());

    if (existingResourceOptional.isPresent()) {

      if (pictureFile != null && !pictureFile.isEmpty()) {
        String uniqueFileName = pictureService.savePicture(pictureFile, false);
        updatedResource.setResourcePictureFileName(uniqueFileName);
      }

      return repository.save(updatedResource);
      
    } else {
        throw new ResourceNotFoundException("Resource with ID " + updatedResource.getId() + " not found");
    }
  }

  public boolean softDeleteResource(Long id) {
    Optional<T> resourceOptional = repository.findById(id);

    if (resourceOptional.isPresent()) {

      T updatedResource = resourceOptional.get();
      updatedResource.setStatus("deleted");

      repository.save(updatedResource);
      return true;
      
    } else {
        throw new ResourceNotFoundException("Resource with ID " + id + " not found");
    }
  }

  public Optional<byte[]> getResourcePictureByResourcename(String resourceName) {
    Optional<T> resource = getResourceByResourceName(resourceName);
    return resource.flatMap(u -> getresourcePicturesByResourceId(u.getId()));
  }

  public Optional<T> getResourceByResourceName(String resourceName) {
    return repository.findAll().stream()
        .filter(u -> u.getName().equals(resourceName))
        .findFirst();
  }

}
