package com.auu_sw3_6.Himmerland_booking_software.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Hospitality;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.HospitalityRepository;

@Service
public class HospitalityService extends ResourceService<Hospitality> {

  @Autowired
  public HospitalityService(HospitalityRepository ResourceRepository, PictureService pictureService) {
    super(ResourceRepository, pictureService);
  }

  public Hospitality createHospitality(Hospitality hospitality, MultipartFile resourcePictures) {
    if (hospitality.getName() == null || hospitality.getName().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null or empty.");
    }

    return createResource(hospitality, resourcePictures);
  }
}