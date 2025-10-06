package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import com.auu_sw3_6.Himmerland_booking_software.api.controller.testSecurityHelpers.SecurityContextHelper;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Resource;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.ResourceType;
import com.auu_sw3_6.Himmerland_booking_software.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractResourceControllerTest<T extends Resource> {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected BookingService bookingservice;

  protected T testResource;

  protected abstract T createTestResource();

  protected abstract String getBasePath();

  @BeforeEach
  void setUp() {
    testResource = createTestResource();
  }

  @Test
  public void getResourceByID_ShouldReturnResource() throws Exception {

    SecurityContextHelper.setSecurityContext("admin");

    MvcResult result = mockMvc.perform(get(getBasePath() + "/" + testResource.getId()))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    T responseResource = objectMapper.readValue(jsonResponse, (Class<T>) testResource.getClass());

    assertEquals(testResource.getName(), responseResource.getName());
    assertEquals(testResource.getDescription(), responseResource.getDescription());
    assertEquals(testResource.getCapacity(), responseResource.getCapacity());
    assertEquals(testResource.getId(), responseResource.getId());
    assertEquals(testResource.getStatus(), responseResource.getStatus());
    assertEquals(testResource.getType(), responseResource.getType());
  }

  @Test
  public void updateResource_ShouldReturnUpdatedResource() throws Exception {

    SecurityContextHelper.setSecurityContext("admin");

    T updatedResource = createTestResource();
    updatedResource.setName("Updated Resource");
    updatedResource.setDescription("Updated Description");
    updatedResource.setCapacity(5);
    updatedResource.setStatus("Available");
    updatedResource.setType(ResourceType.TOOL);

    String updatedResourceJson = objectMapper.writeValueAsString(updatedResource);

    Path imagePath = Paths.get(getClass().getClassLoader().getResource("300x300-test-image.png").toURI());
    byte[] imageBytes = Files.readAllBytes(imagePath);

    MockMultipartFile mockFile = new MockMultipartFile(
        "resourcePictures",
        "testPicture.jpg",
        "image/jpeg",
        imageBytes);

    MockMultipartHttpServletRequestBuilder builder = (MockMultipartHttpServletRequestBuilder) multipart(
        getBasePath() + "/update").with(request -> {
          request.setMethod("PUT");
          return request;
        });

    MvcResult result = mockMvc.perform(builder
        .file(mockFile)
        .file("updatedResource", updatedResourceJson.getBytes())
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    T responseResource = objectMapper.readValue(jsonResponse, (Class<T>) testResource.getClass());

    assertEquals(updatedResource.getName(), responseResource.getName());
    assertEquals(updatedResource.getDescription(), responseResource.getDescription());
    assertEquals(updatedResource.getCapacity(), responseResource.getCapacity());
    assertEquals(updatedResource.getStatus(), responseResource.getStatus());
    assertEquals(updatedResource.getType(), responseResource.getType());
  }

}
