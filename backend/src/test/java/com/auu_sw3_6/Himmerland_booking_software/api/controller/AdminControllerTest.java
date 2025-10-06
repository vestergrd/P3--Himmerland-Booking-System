package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import com.auu_sw3_6.Himmerland_booking_software.api.controller.testSecurityHelpers.SecurityContextHelper;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Admin;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Tenant;
import com.auu_sw3_6.Himmerland_booking_software.service.AdminService;
import com.auu_sw3_6.Himmerland_booking_software.service.TenantService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

public class AdminControllerTest extends AbstractUserControllerTest<Admin> {

  @Autowired
  private TenantService tenantService;

  @Autowired
  private AdminService adminService;

  @Override
  protected Admin createTestUser() {
    Admin admin = new Admin();
    admin.setId(1L);
    admin.setUsername("adminUser");
    admin.setPassword("rawPassword123");
    admin.setEmail("adminEmail@mail.com");
    admin.setMobileNumber("88888888");
    admin.setName("adminName");
    admin.setHouseAddress("adminAddress");
    admin.setProfilePictureFileName("adminPicture");

    return adminService.createUser(admin, null);
  }

  @Override
  protected String getBasePath() {
    return "/api/admin";
  }

  @Override
  protected String getRequiredRole() {
    return "admin";
  }

  @Test
  public void getAllTenants_shouldReturnAllTenants() throws Exception {
    Tenant tenant1 = new Tenant(2L, "tenant1", "tenant@email1.com", "11111111", "Tenant Test", "tenantPass123",
        "tenantpic1.png", "tenantAddress1");
    Tenant tenant2 = new Tenant(3L, "tenant2", "tenant@email2.com", "22222222", "Tenant Test2", "tenantPass456",
        "tenantpic2.png", "tenantAddress2");
    Tenant tenant3 = new Tenant(4L, "tenant3", "tenant@email3.com", "33333333", "Tenant Test3", "tenantPass789",
        "tenantpic3.png", "tenantAddress3");
    tenantService.createUser(tenant1, null);
    tenantService.createUser(tenant2, null);
    tenantService.createUser(tenant3, null);

    SecurityContextHelper.setSecurityContext(testUser, "admin");

    MvcResult result = mockMvc.perform(get(getBasePath() + "/getAllTenants"))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    List<Tenant> responseTenants = objectMapper.readValue(jsonResponse,
        objectMapper.getTypeFactory().constructCollectionType(List.class, Tenant.class));

    assertEquals(3, responseTenants.size());
    assertEquals(tenant1.getUsername(), responseTenants.get(0).getUsername());
    assertEquals(tenant2.getUsername(), responseTenants.get(1).getUsername());
    assertEquals(tenant3.getUsername(), responseTenants.get(2).getUsername());
  }

  @Test
  public void addCaretakerName_shouldAddCaretakerName() throws Exception {
  String caretakerName = "John Doe";

  SecurityContextHelper.setSecurityContext(testUser, "admin");

  mockMvc.perform(put(getBasePath() + "/addCaretakerName")
      .contentType(MediaType.APPLICATION_JSON)
      .content(caretakerName))
      .andExpect(status().isOk());
}

}
