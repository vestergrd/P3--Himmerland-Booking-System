package com.auu_sw3_6.Himmerland_booking_software.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Tool;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.ToolRepository;

@Service
public class ToolService extends ResourceService<Tool> {

  @Autowired
  public ToolService(ToolRepository ResourceRepository, PictureService pictureService) {
    super(ResourceRepository, pictureService);
  }

  public Tool createTool(Tool tool, MultipartFile resourcePictures) {
    if (tool.getName() == null || tool.getName().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null or empty.");
    }

    return createResource(tool, resourcePictures);
  }

}
