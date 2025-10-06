package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Tenant;
import com.auu_sw3_6.Himmerland_booking_software.service.TenantService;

import org.springframework.beans.factory.annotation.Autowired;

public class TenantControllerTest extends AbstractUserControllerTest<Tenant> {

  @Autowired
  private TenantService tenantService;

  @Override
  protected Tenant createTestUser() {
    Tenant tenant = new Tenant();
    tenant.setId(1L);
    tenant.setUsername("testTenant");
    tenant.setPassword("rawPassword123");
    tenant.setEmail("tenantEmail@mail.com");
    tenant.setMobileNumber("88888888");
    tenant.setName("testTenantName");
    tenant.setHouseAddress("tenantAddress");
    tenant.setProfilePictureFileName("tenantPicture");

    return tenantService.createUser(tenant, null);
  }

  @Override
  protected String getBasePath() {
    return "/api/tenant";
  }

  @Override
  protected String getRequiredRole() {
    return "tenant";
  }
}
