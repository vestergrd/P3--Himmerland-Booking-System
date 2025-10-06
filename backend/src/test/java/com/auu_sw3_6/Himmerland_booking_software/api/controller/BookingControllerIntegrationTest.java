package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Booking;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Tool;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Tenant;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.ResourceType;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.BookingRepository;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.ToolRepository;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.TenantRepository;
import com.auu_sw3_6.Himmerland_booking_software.service.BookingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private TenantRepository tenantRepository;

    private Tool testResource;
    private Tenant testTenant;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        toolRepository.deleteAll();
        tenantRepository.deleteAll();

        testTenant = new Tenant();
        testTenant.setName("Test Tenant");
        testTenant.setEmail("testtenant@example.com");
        testTenant.setUsername("testtenant");
        testTenant.setPassword("password123");
        testTenant.setMobileNumber("23456789");
        testTenant = tenantRepository.save(testTenant);

        testResource = new Tool();
        testResource.setName("Test Tool");
        testResource.setDescription("This is a test tool resource");
        testResource.setCapacity(10);
        testResource.setStatus("Available");
        testResource.setType(ResourceType.TOOL);
        testResource = toolRepository.save(testResource);
    }

    @AfterEach
    void cleanUp() {
        bookingRepository.deleteAll();
        toolRepository.deleteAll();
        tenantRepository.deleteAll();
    }

    @WithMockUser(username = "caretaker", roles = {"ADMIN"})
    @Test
    void getBookings_shouldReturnListOfBookings_whenAdminAccesses() throws Exception {
        // Arrange
        Booking booking1 = new Booking();
        booking1.setReceiverName("John Doe");
        booking1.setResource(testResource);
        booking1.setUser(testTenant);
        booking1 = bookingService.createBooking(booking1);

        Booking booking2 = new Booking();
        booking2.setReceiverName("Jane Doe");
        booking2.setResource(testResource);
        booking2.setUser(testTenant);
        booking2 = bookingService.createBooking(booking2);

        // Act & Assert
        mockMvc.perform(get("/api/booking/get-all"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(booking1.getId()))
               .andExpect(jsonPath("$[0].receiverName").value("John Doe"))
               .andExpect(jsonPath("$[1].id").value(booking2.getId()))
               .andExpect(jsonPath("$[1].receiverName").value("Jane Doe"));
    }

    @WithMockUser(username = "caretaker", roles = {"ADMIN"})
    @Test
    void getBookings_shouldReturnNotFound_whenNoBookingsExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/booking/get-all"))
               .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleMultipleNegativeScenariosInOneRequest() throws Exception {
        // Arrange:
        bookingRepository.deleteAll();

        // Act:
        mockMvc.perform(get("/api/booking/get-all").with(user(testTenant.getUsername()).roles("TENANT")))
               // Assert:
               .andExpect(status().isForbidden());
    }
}
