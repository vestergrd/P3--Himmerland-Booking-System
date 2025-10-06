package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.auu_sw3_6.Himmerland_booking_software.api.controller.testSecurityHelpers.SecurityContextHelper;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Hospitality;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.ResourceType;
import com.auu_sw3_6.Himmerland_booking_software.service.HospitalityService;

public class HospitalityControllerTest extends AbstractResourceControllerTest<Hospitality> {

  @Autowired
  private HospitalityService hospitalityService;

  @Override
  protected Hospitality createTestResource() {
    Hospitality testHospitality = new Hospitality();
    testHospitality.setName("Test Hospitality");
    testHospitality.setDescription("Test Description");
    testHospitality.setCapacity(2);
    testHospitality.setId(1l);
    testHospitality.setStatus("Available");
    testHospitality.setType(ResourceType.HOSPITALITY);

    return hospitalityService.createHospitality(testHospitality, null);
  }

  @Override
  protected String getBasePath() {
    return "/api/hospitality";
  }

  @Test
  public void createHospitality_shouldReturnHospitality() throws Exception {
    SecurityContextHelper.setSecurityContext("admin");

    Hospitality hospitality = new Hospitality();
    hospitality.setName("Test Hospitality");
    hospitality.setDescription("Test Description");
    hospitality.setCapacity(10);
    hospitality.setStatus("Available");
    hospitality.setType(ResourceType.HOSPITALITY);

    String hospitalityJson = objectMapper.writeValueAsString(hospitality);

    Path imagePath = Paths.get(getClass().getClassLoader().getResource("300x300-test-image.png").toURI());
    byte[] imageBytes = Files.readAllBytes(imagePath);

    MockMultipartFile imageFile = new MockMultipartFile(
        "resourcePictures",
        "300x300-test-image.png",
        MediaType.IMAGE_PNG_VALUE,
        imageBytes);

    MockMultipartFile hospitalityPart = new MockMultipartFile(
        "hospitality",
        "hospitality.json",
        MediaType.APPLICATION_JSON_VALUE,
        hospitalityJson.getBytes());

    MvcResult result = mockMvc.perform(multipart(getBasePath() + "/create")
        .file(imageFile)
        .file(hospitalityPart)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    Hospitality responseHospitality = objectMapper.readValue(jsonResponse, Hospitality.class);

    System.out.println("Response: " + jsonResponse);

    assertEquals(hospitality.getName(), responseHospitality.getName());
    assertEquals(hospitality.getDescription(), responseHospitality.getDescription());
    assertEquals(hospitality.getCapacity(), responseHospitality.getCapacity());
    assertEquals(hospitality.getStatus(), responseHospitality.getStatus());
    assertEquals(hospitality.getType(), responseHospitality.getType());
  }

}
