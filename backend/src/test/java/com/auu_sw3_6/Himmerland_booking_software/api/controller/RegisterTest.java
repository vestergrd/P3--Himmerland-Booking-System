package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import com.auu_sw3_6.Himmerland_booking_software.api.controller.testSecurityHelpers.SecurityContextHelper;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Admin;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Booking;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Tenant;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Tool;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.BookingStatus;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.ResourceType;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.TimeRange;
import com.auu_sw3_6.Himmerland_booking_software.service.TenantService;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.BookingRepository;
import com.auu_sw3_6.Himmerland_booking_software.service.BookingService;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Resource;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.ToolRepository;
import com.auu_sw3_6.Himmerland_booking_software.config.security.CustomUserDetails;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.TenantRepository;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.ResourceRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RegisterTest {

    @Autowired
    private MockMvc mockMvc;

    private Tenant testTenant;

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
void setUp() {

    bookingRepository.deleteAll();
    toolRepository.deleteAll();
    tenantRepository.deleteAll();

    testTenant = new Tenant();
    testTenant.setName("Test Tenant");
    testTenant.setEmail("testtenant@example.com");
    testTenant.setUsername("testtenant");
    testTenant.setPassword(passwordEncoder.encode("password123"));
    testTenant.setMobileNumber("23456789");
    testTenant = tenantRepository.save(testTenant);

    Tool testResource = new Tool();
    testResource.setName("Test Tool");
    testResource.setDescription("This is a test tool resource");
    testResource.setCapacity(10);
    testResource.setStatus("Available");
    testResource.setType(ResourceType.TOOL);
    testResource = toolRepository.save(testResource);

    Booking testBooking = new Booking();
    testBooking.setResource(testResource);
    testBooking.setUser(testTenant);
    testBooking.setStartDate(LocalDate.of(2034, 2, 1));
    testBooking.setEndDate(LocalDate.of(2034, 2, 5));
    testBooking.setPickupTime(TimeRange.EARLY);
    testBooking.setDropoffTime(TimeRange.LATE);
    testBooking.setStatus(BookingStatus.CONFIRMED);
    testBooking.setReceiverName("Receiver Name");
    bookingRepository.save(testBooking);
    testBooking.setResource(testResource);
    testBooking.setUser(testTenant);     

}

@AfterEach
void cleanUp() {
    bookingRepository.deleteAll();
    toolRepository.deleteAll();
    tenantRepository.deleteAll();
}


@Test
void testRegisterTenant_Positive() throws Exception {
    // Arrange
    MockMultipartFile userPart = new MockMultipartFile(
            "user",
            "",
            "application/json",
            ("{\"name\":\"New Tenant\",\"email\":\"newtenant@example.com\","
                    + "\"username\":\"newtenant\",\"password\":\"password123\","
                    + "\"mobileNumber\":\"12345678\"}").getBytes()
    );

    // Act
    mockMvc.perform(
            multipart("/api/tenant/register")
                    .file(userPart)
                    .contentType(MediaType.MULTIPART_FORM_DATA))

            // Assert
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("New Tenant"))
            .andExpect(jsonPath("$.email").value("newtenant@example.com"));
}

@Test
void testRegisterTenant_Negative_InvalidEmail() throws Exception {
    // Arrange
    MockMultipartFile userPart = new MockMultipartFile(
            "user",
            "",
            "application/json",
            ("{\"name\":\"New Tenant\",\"email\":\"invalid-email\","
                    + "\"username\":\"newtenant\",\"password\":\"password123\","
                    + "\"mobileNumber\":\"12345678\"}").getBytes()
    );

    // Act
    mockMvc.perform(
            multipart("/api/tenant/register")
                    .file(userPart)
                    .contentType(MediaType.MULTIPART_FORM_DATA))

            // Assert
            .andExpect(status().isBadRequest());
}

@Test
void testGetOwnBookings_Positive() throws Exception {
    // Arrange
    CustomUserDetails userDetails = new CustomUserDetails(testTenant, List.of(() -> "ROLE_TENANT"));
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);
    // Act
    mockMvc.perform(get("/api/tenant/getOwnBookings"))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].resource.name").value("Test Tool"))
            .andExpect(jsonPath("$[0].status").value("CONFIRMED"));
}

@Test
void testGetOwnBookings_Negative_Unauthorized() throws Exception {
    // Act
    mockMvc.perform(get("/api/tenant/getOwnBookings"))
            // Assert
            .andExpect(status().isUnauthorized());
}

}


