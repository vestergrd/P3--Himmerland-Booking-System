package com.auu_sw3_6.Himmerland_booking_software.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Utility;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.UtilityRepository;

@Service
public class UtilityService extends ResourceService<Utility> {

  @Autowired
  public UtilityService(UtilityRepository ResourceRepository, PictureService pictureService) {
    super(ResourceRepository, pictureService);
  }

  public Utility createUtility(Utility utility, MultipartFile resourcePictures) {
    if (utility.getName() == null || utility.getName().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null or empty.");
    }

    if (utility.getCapacity() < 0) {
        throw new IllegalArgumentException("Capacity must be greater than 0.");
    }

    return createResource(utility, resourcePictures);
  }

}