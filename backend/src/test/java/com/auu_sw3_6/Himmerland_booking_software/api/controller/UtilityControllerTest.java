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
import com.auu_sw3_6.Himmerland_booking_software.api.model.Utility;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.ResourceType;
import com.auu_sw3_6.Himmerland_booking_software.service.UtilityService;

public class UtilityControllerTest extends AbstractResourceControllerTest<Utility> {

  @Autowired
  private UtilityService utilityService;

  @Override
  protected Utility createTestResource() {
    Utility testUtility = new Utility();
    testUtility.setName("Test Utility");
    testUtility.setDescription("Test Description");
    testUtility.setCapacity(2);
    testUtility.setId(1l);
    testUtility.setStatus("Available");
    testUtility.setType(ResourceType.UTILITY);

    return utilityService.createUtility(testUtility, null);
  }

  @Override
  protected String getBasePath() {
    return "/api/utility";
  }

  @Test
  public void createUtility_shouldReturnUtility() throws Exception {
    SecurityContextHelper.setSecurityContext("admin");

    Utility utility = new Utility();
    utility.setName("Test Utility");
    utility.setDescription("Test Description");
    utility.setCapacity(10);
    utility.setStatus("Available");
    utility.setType(ResourceType.UTILITY);

    String utilityJson = objectMapper.writeValueAsString(utility);

    Path imagePath = Paths.get(getClass().getClassLoader().getResource("300x300-test-image.png").toURI());
    byte[] imageBytes = Files.readAllBytes(imagePath);

    MockMultipartFile imageFile = new MockMultipartFile(
        "resourcePictures",
        "300x300-test-image.png",
        MediaType.IMAGE_PNG_VALUE,
        imageBytes);

    MockMultipartFile utilityPart = new MockMultipartFile(
        "utility",
        "utility.json",
        MediaType.APPLICATION_JSON_VALUE,
        utilityJson.getBytes());

    MvcResult result = mockMvc.perform(multipart(getBasePath() + "/create")
        .file(imageFile)
        .file(utilityPart)
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    Utility responseUtility = objectMapper.readValue(jsonResponse, Utility.class);

    System.out.println("Response: " + jsonResponse);

    assertEquals(utility.getName(), responseUtility.getName());
    assertEquals(utility.getDescription(), responseUtility.getDescription());
    assertEquals(utility.getCapacity(), responseUtility.getCapacity());
    assertEquals(utility.getStatus(), responseUtility.getStatus());
    assertEquals(utility.getType(), responseUtility.getType());
  }

}