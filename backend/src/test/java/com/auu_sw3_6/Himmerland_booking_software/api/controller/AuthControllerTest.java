package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.auu_sw3_6.Himmerland_booking_software.api.controller.testSecurityHelpers.SecurityContextHelper;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Admin;
import com.auu_sw3_6.Himmerland_booking_software.api.model.LoginRequest;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Tenant;
import com.auu_sw3_6.Himmerland_booking_software.service.AdminService;
import com.auu_sw3_6.Himmerland_booking_software.service.TenantService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

  @Autowired
  private TenantService tenantService;

  @Autowired
  private AdminService adminService;

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  private Admin admin;

  private Tenant tenant;

  @BeforeEach
  public void setUp() {
    admin = new Admin();
    admin.setId(1L);
    admin.setUsername("adminUser");
    admin.setPassword("rawPassword123");
    admin.setEmail("adminEmail@mail.com");
    admin.setMobileNumber("88888888");
    admin.setName("adminName");
    admin.setHouseAddress("adminAddress");
    admin.setProfilePictureFileName("adminPicture");

    adminService.createUser(admin, null);

    tenant = new Tenant();
    tenant.setId(2L);
    tenant.setUsername("testTenant");
    tenant.setPassword("rawPassword321");
    tenant.setEmail("tenantEmail@mail.com");
    tenant.setMobileNumber("88888888");
    tenant.setName("testTenantName");
    tenant.setHouseAddress("tenantAddress");
    tenant.setProfilePictureFileName("tenantPicture");

    tenantService.createUser(tenant, null);
  }

  @Test
  public void validAdminLoginRequest_shouldLogin_AndSendCookies() throws Exception {

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername("adminUser");
    loginRequest.setPassword("rawPassword123");

    String toolJson = objectMapper.writeValueAsString(loginRequest);

    MvcResult result = mockMvc.perform(post("/api/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(toolJson))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();

    Map<String, String> responseMap = objectMapper.readValue(jsonResponse, Map.class);

    System.out.println("Response: " + jsonResponse);

    assertEquals("Login successful", responseMap.get("message"));

    List<String> cookieHeaders = result.getResponse().getHeaders("Set-Cookie");

    assertNotNull(cookieHeaders);
    assertEquals(2, cookieHeaders.size());

    String jwtCookie = cookieHeaders.stream().filter(c -> c.startsWith("jwt=")).findFirst().orElse(null);
    assertNotNull(jwtCookie);
    assertTrue(jwtCookie.contains("HttpOnly"));
    assertTrue(jwtCookie.contains("Secure"));
    assertTrue(jwtCookie.contains("SameSite=none"));
    assertTrue(jwtCookie.contains("Max-Age=18000"));

    String authIndicatorCookie = cookieHeaders.stream().filter(c -> c.startsWith("authIndicator=")).findFirst()
        .orElse(null);
    assertNotNull(authIndicatorCookie);
    assertFalse(authIndicatorCookie.contains("HttpOnly"));
    assertTrue(authIndicatorCookie.contains("Secure"));
    assertTrue(authIndicatorCookie.contains("SameSite=none"));
    assertTrue(authIndicatorCookie.contains("Max-Age=18000"));

    assertTrue(authIndicatorCookie.contains("ROLE_ADMIN"));

  }

  @Test
  public void invalidAdminLogin_shouldReturn_Unauthorized() throws Exception {

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername("adminUser");
    loginRequest.setPassword("wrongPassword123");

    String toolJson = objectMapper.writeValueAsString(loginRequest);

    MvcResult result = mockMvc.perform(post("/api/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(toolJson))
        .andExpect(status().isUnauthorized())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();

    Map<String, String> responseMap = objectMapper.readValue(jsonResponse, Map.class);

    System.out.println("Response: " + jsonResponse);

    assertEquals("Invalid credentials", responseMap.get("error"));

    List<String> cookieHeaders = result.getResponse().getHeaders("Set-Cookie");

    assertNotNull(cookieHeaders);
    assertEquals(0, cookieHeaders.size());
  }

  @Test
  public void validTenantLogin_shouldReturn_CookieJWTToken() throws Exception {

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername("testTenant");
    loginRequest.setPassword("rawPassword321");

    String toolJson = objectMapper.writeValueAsString(loginRequest);

    MvcResult result = mockMvc.perform(post("/api/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(toolJson))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();

    Map<String, String> responseMap = objectMapper.readValue(jsonResponse, Map.class);

    System.out.println("Response: " + jsonResponse);

    assertEquals("Login successful", responseMap.get("message"));

    List<String> cookieHeaders = result.getResponse().getHeaders("Set-Cookie");

    assertNotNull(cookieHeaders);
    assertEquals(2, cookieHeaders.size());

    String jwtCookie = cookieHeaders.stream().filter(c -> c.startsWith("jwt=")).findFirst().orElse(null);
    assertNotNull(jwtCookie);
    assertTrue(jwtCookie.contains("HttpOnly"));
    assertTrue(jwtCookie.contains("Secure"));
    assertTrue(jwtCookie.contains("SameSite=none"));
    assertTrue(jwtCookie.contains("Max-Age=18000"));

    String authIndicatorCookie = cookieHeaders.stream().filter(c -> c.startsWith("authIndicator=")).findFirst()
        .orElse(null);
    assertNotNull(authIndicatorCookie);
    assertFalse(authIndicatorCookie.contains("HttpOnly"));
    assertTrue(authIndicatorCookie.contains("Secure"));
    assertTrue(authIndicatorCookie.contains("SameSite=none"));
    assertTrue(authIndicatorCookie.contains("Max-Age=18000"));

    assertTrue(authIndicatorCookie.contains("ROLE_TENANT"));
  }

  @Test
  public void invalidTenantLogin_shouldReturn_Unauthorized() throws Exception {

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setUsername("testTenant");
    loginRequest.setPassword("wrongPassword321");

    String toolJson = objectMapper.writeValueAsString(loginRequest);

    MvcResult result = mockMvc.perform(post("/api/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(toolJson))
        .andExpect(status().isUnauthorized())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();

    Map<String, String> responseMap = objectMapper.readValue(jsonResponse, Map.class);

    System.out.println("Response: " + jsonResponse);

    assertEquals("Invalid credentials", responseMap.get("error"));

    List<String> cookieHeaders = result.getResponse().getHeaders("Set-Cookie");

    assertNotNull(cookieHeaders);
    assertEquals(0, cookieHeaders.size());

  }

  @Test
  public void logoutForAdmin_shouldReturn_EmptyJWTToken_AndClearSecurityContext() throws Exception {

    SecurityContextHelper.setSecurityContext(admin, "admin");

    MvcResult result = mockMvc.perform(post("/api/logout"))
        .andExpect(status().isOk())
        .andReturn();

    List<String> cookieHeaders = result.getResponse().getHeaders("Set-Cookie");

    assertNotNull(cookieHeaders);
    assertEquals(1, cookieHeaders.size());

    String jwtCookie = cookieHeaders.stream().filter(c -> c.startsWith("jwt=")).findFirst().orElse(null);
    assertNotNull(jwtCookie);
    assertTrue(jwtCookie.contains("HttpOnly"));
    assertTrue(jwtCookie.contains("Secure"));
    assertTrue(jwtCookie.contains("SameSite=none"));
    assertTrue(jwtCookie.contains("Max-Age=0"));

    assertEquals(SecurityContextHolder.getContext().getAuthentication(), null);
  }

  @Test
  public void logoutForTenant_shouldReturn_EmptyJWTToken_AndClearSecurityContext() throws Exception {

    SecurityContextHelper.setSecurityContext(tenant, "tenant");

    MvcResult result = mockMvc.perform(post("/api/logout"))
        .andExpect(status().isOk())
        .andReturn();

    List<String> cookieHeaders = result.getResponse().getHeaders("Set-Cookie");

    assertNotNull(cookieHeaders);
    assertEquals(1, cookieHeaders.size());

    String jwtCookie = cookieHeaders.stream().filter(c -> c.startsWith("jwt=")).findFirst().orElse(null);
    assertNotNull(jwtCookie);
    assertTrue(jwtCookie.contains("HttpOnly"));
    assertTrue(jwtCookie.contains("Secure"));
    assertTrue(jwtCookie.contains("SameSite=none"));
    assertTrue(jwtCookie.contains("Max-Age=0"));

    assertEquals(SecurityContextHolder.getContext().getAuthentication(), null);
  }


}