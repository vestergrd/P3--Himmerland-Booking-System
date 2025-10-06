package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import com.auu_sw3_6.Himmerland_booking_software.api.controller.testSecurityHelpers.SecurityContextHelper;
import com.auu_sw3_6.Himmerland_booking_software.api.controller.testSecurityHelpers.TestTimeProviderConfig;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Booking;
import com.auu_sw3_6.Himmerland_booking_software.api.model.BookingDetails;
import com.auu_sw3_6.Himmerland_booking_software.api.model.ErrorResponse;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Resource;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Tool;
import com.auu_sw3_6.Himmerland_booking_software.api.model.User;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.BookingStatus;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.ResourceType;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.TimeRange;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.BookingRepository;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.ToolRepository;
import com.auu_sw3_6.Himmerland_booking_software.exception.BookingError;
import com.auu_sw3_6.Himmerland_booking_software.service.TimeProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestTimeProviderConfig.class)
public abstract class AbstractUserControllerTest<T extends User> {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected BookingRepository bookingRepository;

  protected T testUser;

  protected abstract T createTestUser();

  protected abstract String getBasePath();

  protected abstract String getRequiredRole();

  protected LocalTime now;
  protected LocalDate mondayToday;
  protected Tool savedTool;

  @Autowired
  protected TimeProvider timeProvider;

  @Autowired
  protected ToolRepository toolRepository;

  protected LocalDate getNextNonWeekendDay(LocalDate currentDate) {
    DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
    if (dayOfWeek == DayOfWeek.FRIDAY) {
      return currentDate.plusDays(3);
    } else if (dayOfWeek == DayOfWeek.SATURDAY) {
      return currentDate.plusDays(2);
    } else if (dayOfWeek == DayOfWeek.SUNDAY) {
      return currentDate.plusDays(1);
    } else {
      return currentDate.plusDays(1);
    }
  }
  
  @BeforeEach
  public void setUp() {

    SecurityContextHelper.clearSecurityContext();
    bookingRepository.deleteAll();
    toolRepository.deleteAll();

    mondayToday = timeProvider.getToday();
    now = timeProvider.getNow();

    testUser = createTestUser();

    Tool tool = new Tool();
    tool.setName("Test Tool");
    tool.setCapacity(1);
    tool.setDescription("Test Description");
    tool.setStatus("Available");
    tool.setResourcePictureFileName("test.jpg");
    tool.setType(ResourceType.TOOL);

    savedTool = toolRepository.save(tool);
    assertTrue(toolRepository.existsById(savedTool.getId()));
  }

  @Test
  public void getUser_shouldReturnCurrentUser() throws Exception {
    SecurityContextHelper.setSecurityContext(testUser, getRequiredRole());

    MvcResult result = mockMvc.perform(get(getBasePath()))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    T responseUser = objectMapper.readValue(jsonResponse, (Class<T>) testUser.getClass());

    assertEquals(testUser.getName(), responseUser.getName());
    assertEquals(testUser.getMobileNumber(), responseUser.getMobileNumber());
    assertEquals(testUser.getEmail(), responseUser.getEmail());
    assertEquals(testUser.getUsername(), responseUser.getUsername());
    assertEquals(testUser.getHouseAddress(), responseUser.getHouseAddress());
  }

  @Test
  public void getUser_withoutAuthorization_shouldReturnUnauthorized() throws Exception {
    mockMvc.perform(get(getBasePath()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void getUser_withInvalidRole_shouldReturnForbidden() throws Exception {
    SecurityContextHelper.setSecurityContext(testUser, "INVALID_ROLE");

    mockMvc.perform(get(getBasePath()))
        .andExpect(status().isForbidden());
  }

  @Test
  public void createBooking_ShouldReturn_Booking() throws Exception {

    SecurityContextHelper.setSecurityContext(testUser, getRequiredRole());

    BookingDetails bookingDetails = new BookingDetails();
    bookingDetails.setResourceID(savedTool.getId());
    bookingDetails.setResourceType(ResourceType.TOOL);
    bookingDetails.setStartDate(mondayToday);
    bookingDetails.setEndDate(mondayToday.plusDays(4));
    bookingDetails.setPickupTime(TimeRange.LATE);
    bookingDetails.setDropoffTime(TimeRange.LATE);

    String bookingDetailsJson = objectMapper.writeValueAsString(bookingDetails);

    MvcResult result = mockMvc.perform(post(getBasePath() + "/book-resource")
        .contentType(MediaType.APPLICATION_JSON)
        .content(bookingDetailsJson))
        .andExpect(status().isOk())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();

    SimpleModule module = new SimpleModule();
    module.addAbstractTypeMapping(Resource.class, Tool.class);
    module.addAbstractTypeMapping(User.class, testUser.getClass());
    objectMapper.registerModule(module);

    Booking responseBooking = objectMapper.readValue(jsonResponse,
        Booking.class);

    assertEquals(responseBooking.getDropoffTime(), bookingDetails.getDropoffTime());
    assertEquals(responseBooking.getPickupTime(), bookingDetails.getPickupTime());
    assertEquals(responseBooking.getStartDate(), bookingDetails.getStartDate());
    assertEquals(responseBooking.getEndDate(), bookingDetails.getEndDate());
    assertEquals(responseBooking.getStatus(), BookingStatus.PENDING);
    assertEquals(responseBooking.getUser().getId(), testUser.getId());
    assertEquals(responseBooking.getResource().getId(), bookingDetails.getResourceID());
  }

  @Test
  public void createBooking_withInvalidRole_shouldReturnForbidden() throws Exception {

    SecurityContextHelper.setSecurityContext(testUser, "INVALID_ROLE");

    BookingDetails bookingDetails = new BookingDetails();
    bookingDetails.setResourceID(savedTool.getId());
    bookingDetails.setResourceType(ResourceType.TOOL);
    bookingDetails.setStartDate(mondayToday);
    bookingDetails.setEndDate(mondayToday.plusDays(4));
    bookingDetails.setPickupTime(TimeRange.EARLY);
    bookingDetails.setDropoffTime(TimeRange.LATE);

    String bookingDetailsJson = objectMapper.writeValueAsString(bookingDetails);

    MvcResult result = mockMvc.perform(post(getBasePath() + "/book-resource")
        .contentType(MediaType.APPLICATION_JSON)
        .content(bookingDetailsJson))
        .andExpect(status().isForbidden())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    System.out.println("Response: " + jsonResponse);
  }

  @Test
  public void createBooking_ShouldReturn_InvalidDateRangeErrorForStartDateBeforeEnddate() throws Exception {

    SecurityContextHelper.setSecurityContext(testUser, getRequiredRole());

    BookingDetails bookingDetails = new BookingDetails();
    bookingDetails.setResourceID(savedTool.getId());
    bookingDetails.setResourceType(ResourceType.TOOL);
    bookingDetails.setStartDate(mondayToday.plusDays(4));
    bookingDetails.setEndDate(mondayToday);
    bookingDetails.setPickupTime(TimeRange.EARLY);
    bookingDetails.setDropoffTime(TimeRange.LATE);

    String bookingDetailsJson = objectMapper.writeValueAsString(bookingDetails);

    MvcResult result = mockMvc.perform(post(getBasePath() + "/book-resource")
        .contentType(MediaType.APPLICATION_JSON)
        .content(bookingDetailsJson))
        .andExpect(status().isBadRequest())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();

    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);

    assertNotNull(errorResponse.getDetails());
    assertEquals("Illegal booking", errorResponse.getMessage());
    assertEquals(400, errorResponse.getStatus());

    Map<String, String> details = (Map<String, String>) errorResponse.getDetails();
    assertEquals(BookingError.INVALID_DATE_RANGE.getErrorCode(), Integer.parseInt(details.get("errorcode")));
  }

  @Test
  public void createBooking_ShouldReturn_InvalidDateForRangeLimit() throws Exception {

    SecurityContextHelper.setSecurityContext(testUser, getRequiredRole());

    BookingDetails bookingDetails = new BookingDetails();
    bookingDetails.setResourceID(savedTool.getId());
    bookingDetails.setResourceType(ResourceType.TOOL);
    bookingDetails.setStartDate(mondayToday);
    bookingDetails.setEndDate(mondayToday.plusDays(7));
    bookingDetails.setPickupTime(TimeRange.EARLY);
    bookingDetails.setDropoffTime(TimeRange.LATE);

    String bookingDetailsJson = objectMapper.writeValueAsString(bookingDetails);

    MvcResult result = mockMvc.perform(post(getBasePath() + "/book-resource")
        .contentType(MediaType.APPLICATION_JSON)
        .content(bookingDetailsJson))
        .andExpect(status().isBadRequest())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();

    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);

    assertNotNull(errorResponse.getDetails());
    assertEquals("Illegal booking", errorResponse.getMessage());
    assertEquals(400, errorResponse.getStatus());

    Map<String, String> details = (Map<String, String>) errorResponse.getDetails();
    assertEquals(BookingError.INVALID_DATE_RANGE.getErrorCode(), Integer.parseInt(details.get("errorcode")));
  }

  @Test
  public void createBooking_ShouldReturn_StartDateInPastError() throws Exception {
    SecurityContextHelper.setSecurityContext(testUser, getRequiredRole());

    BookingDetails bookingDetails = new BookingDetails();
    bookingDetails.setResourceID(savedTool.getId());
    bookingDetails.setResourceType(ResourceType.TOOL);
    bookingDetails.setStartDate(mondayToday.minusDays(1));
    bookingDetails.setEndDate(getNextNonWeekendDay(mondayToday));
    bookingDetails.setPickupTime(TimeRange.EARLY);
    bookingDetails.setDropoffTime(TimeRange.LATE);

    String bookingDetailsJson = objectMapper.writeValueAsString(bookingDetails);

    MvcResult result = mockMvc.perform(post(getBasePath() + "/book-resource")
        .contentType(MediaType.APPLICATION_JSON)
        .content(bookingDetailsJson))
        .andExpect(status().isBadRequest())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);

    assertNotNull(errorResponse.getDetails());
    assertEquals("Illegal booking", errorResponse.getMessage());
    assertEquals(400, errorResponse.getStatus());

    Map<String, String> details = (Map<String, String>) errorResponse.getDetails();
    assertEquals(BookingError.START_DATE_IN_PAST.getErrorCode(), Integer.parseInt(details.get("errorcode")));
  }

  @Test
  public void createBooking_ShouldReturn_PickupTimeInPastError() throws Exception {
    SecurityContextHelper.setSecurityContext(testUser, getRequiredRole());

    BookingDetails bookingDetails = new BookingDetails();
    bookingDetails.setResourceID(savedTool.getId());
    bookingDetails.setResourceType(ResourceType.TOOL);
    bookingDetails.setStartDate(mondayToday);
    bookingDetails.setEndDate(mondayToday.plusDays(3));
    bookingDetails.setPickupTime(TimeRange.EARLY);
    bookingDetails.setDropoffTime(TimeRange.LATE);

    String bookingDetailsJson = objectMapper.writeValueAsString(bookingDetails);

    MvcResult result = mockMvc.perform(post(getBasePath() + "/book-resource")
        .contentType(MediaType.APPLICATION_JSON)
        .content(bookingDetailsJson))
        .andExpect(status().isBadRequest())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);

    assertNotNull(errorResponse.getDetails());
    assertEquals("Illegal booking", errorResponse.getMessage());
    assertEquals(400, errorResponse.getStatus());

    Map<String, String> details = (Map<String, String>) errorResponse.getDetails();
    assertEquals(BookingError.PICKUP_TIME_IN_PAST.getErrorCode(), Integer.parseInt(details.get("errorcode")));
  }

  @Test
  public void createBooking_ShouldReturn_WeekendBookingError() throws Exception {
    SecurityContextHelper.setSecurityContext(testUser, getRequiredRole());

    BookingDetails bookingDetails = new BookingDetails();
    bookingDetails.setResourceID(savedTool.getId());
    bookingDetails.setResourceType(ResourceType.TOOL);
    bookingDetails.setStartDate(mondayToday.plusDays(4));
    bookingDetails.setEndDate(mondayToday.plusDays(6));
    bookingDetails.setPickupTime(TimeRange.EARLY);
    bookingDetails.setDropoffTime(TimeRange.LATE);

    String bookingDetailsJson = objectMapper.writeValueAsString(bookingDetails);

    MvcResult result = mockMvc.perform(post(getBasePath() + "/book-resource")
        .contentType(MediaType.APPLICATION_JSON)
        .content(bookingDetailsJson))
        .andExpect(status().isBadRequest())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);

    assertNotNull(errorResponse.getDetails());
    assertEquals("Illegal booking", errorResponse.getMessage());
    assertEquals(400, errorResponse.getStatus());

    Map<String, String> details = (Map<String, String>) errorResponse.getDetails();
    assertEquals(BookingError.WEEKEND_BOOKING_NOT_ALLOWED.getErrorCode(), Integer.parseInt(details.get("errorcode")));
  }

  @Test
  public void createBooking_ShouldReturn_TooManyBookingsError() throws Exception {
    SecurityContextHelper.setSecurityContext(testUser, getRequiredRole());

    BookingDetails bookingDetails = new BookingDetails();
    bookingDetails.setResourceID(savedTool.getId());
    bookingDetails.setResourceType(ResourceType.TOOL);
    bookingDetails.setStartDate(mondayToday);
    bookingDetails.setEndDate(mondayToday.plusDays(4));
    bookingDetails.setPickupTime(TimeRange.LATE);
    bookingDetails.setDropoffTime(TimeRange.LATE);

    String bookingDetailsJson = objectMapper.writeValueAsString(bookingDetails);

    for (int i = 0; i < 5; i++) {
      mockMvc.perform(post(getBasePath() + "/book-resource")
          .contentType(MediaType.APPLICATION_JSON)
          .content(bookingDetailsJson))
          .andExpect(status().isOk())
          .andReturn();

      bookingDetails.setStartDate(mondayToday.plusDays(7 * (i + 1) * 3));
      bookingDetails.setEndDate(mondayToday.plusDays(4 + 7 * (i + 1) * 3));
      bookingDetailsJson = objectMapper.writeValueAsString(bookingDetails);
    }

    MvcResult result = mockMvc.perform(post(getBasePath() + "/book-resource")
        .contentType(MediaType.APPLICATION_JSON)
        .content(bookingDetailsJson))
        .andExpect(status().isBadRequest())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);

    assertNotNull(errorResponse.getDetails());
    assertEquals("Illegal booking", errorResponse.getMessage());
    assertEquals(400, errorResponse.getStatus());

    Map<String, String> details = (Map<String, String>) errorResponse.getDetails();
    assertEquals(BookingError.TOO_MANY_BOOKINGS.getErrorCode(), Integer.parseInt(details.get("errorcode")));
  }

  @Test
  public void createBooking_ShouldReturn_NotAvailableError() throws Exception {
    SecurityContextHelper.setSecurityContext(testUser, getRequiredRole());

    BookingDetails bookingDetails = new BookingDetails();
    bookingDetails.setResourceID(savedTool.getId());
    bookingDetails.setResourceType(ResourceType.TOOL);
    bookingDetails.setStartDate(mondayToday);
    bookingDetails.setEndDate(mondayToday.plusDays(4));
    bookingDetails.setPickupTime(TimeRange.LATE);
    bookingDetails.setDropoffTime(TimeRange.LATE);

    String bookingDetailsJson = objectMapper.writeValueAsString(bookingDetails);

    mockMvc.perform(post(getBasePath() + "/book-resource")
        .contentType(MediaType.APPLICATION_JSON)
        .content(bookingDetailsJson))
        .andExpect(status().isOk())
        .andReturn();

    bookingDetails.setStartDate(mondayToday);
    bookingDetails.setEndDate(mondayToday.plusDays(4));
    bookingDetailsJson = objectMapper.writeValueAsString(bookingDetails);

    MvcResult result = mockMvc.perform(post(getBasePath() + "/book-resource")
        .contentType(MediaType.APPLICATION_JSON)
        .content(bookingDetailsJson))
        .andExpect(status().isBadRequest())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);

    assertNotNull(errorResponse.getDetails());
    assertEquals("Illegal booking", errorResponse.getMessage());
    assertEquals(400, errorResponse.getStatus());

    Map<String, String> details = (Map<String, String>) errorResponse.getDetails();
    assertEquals(BookingError.DEFAULT_ERROR.getErrorCode(), Integer.parseInt(details.get("errorcode")));
  }

  @Test
  public void createBooking_ShouldReturn_TooOftenBookingsError() throws Exception {
    SecurityContextHelper.setSecurityContext(testUser, getRequiredRole());

    BookingDetails bookingDetails = new BookingDetails();
    bookingDetails.setResourceID(savedTool.getId());
    bookingDetails.setResourceType(ResourceType.TOOL);
    bookingDetails.setStartDate(mondayToday);
    bookingDetails.setEndDate(mondayToday.plusDays(4));
    bookingDetails.setPickupTime(TimeRange.LATE);
    bookingDetails.setDropoffTime(TimeRange.LATE);

    String bookingDetailsJson = objectMapper.writeValueAsString(bookingDetails);

    mockMvc.perform(post(getBasePath() + "/book-resource")
        .contentType(MediaType.APPLICATION_JSON)
        .content(bookingDetailsJson))
        .andExpect(status().isOk())
        .andReturn();

    bookingDetails.setStartDate(mondayToday.plusDays(7));
    bookingDetails.setEndDate(mondayToday.plusDays(11));
    bookingDetailsJson = objectMapper.writeValueAsString(bookingDetails);

    MvcResult result = mockMvc.perform(post(getBasePath() + "/book-resource")
        .contentType(MediaType.APPLICATION_JSON)
        .content(bookingDetailsJson))
        .andExpect(status().isBadRequest())
        .andReturn();

    String jsonResponse = result.getResponse().getContentAsString();
    ErrorResponse errorResponse = objectMapper.readValue(jsonResponse, ErrorResponse.class);

    assertNotNull(errorResponse.getDetails());
    assertEquals("Illegal booking", errorResponse.getMessage());
    assertEquals(400, errorResponse.getStatus());

    Map<String, String> details = (Map<String, String>) errorResponse.getDetails();
    assertEquals(BookingError.TOO_OFTEN_BOOKING.getErrorCode(), Integer.parseInt(details.get("errorcode")));
  }

}
