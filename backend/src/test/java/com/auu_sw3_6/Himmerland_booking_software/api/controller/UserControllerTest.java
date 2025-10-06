package com.auu_sw3_6.Himmerland_booking_software.api.controller;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Booking;
import com.auu_sw3_6.Himmerland_booking_software.api.model.BookingDetails;
import com.auu_sw3_6.Himmerland_booking_software.api.model.User;
import com.auu_sw3_6.Himmerland_booking_software.exception.UserNotFoundException;
import com.auu_sw3_6.Himmerland_booking_software.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    // Concrete implementation of User for testing
    private static class TestUser extends User {

    }

    @Mock
    private UserService<TestUser> userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    UserController userController = new UserController<TestUser>(userService) {
    };

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController<TestUser>(userService) {
        };
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        Long userId = 1L;
        TestUser updatedUser = new TestUser();
        when(userService.updateUser(userId, updatedUser)).thenReturn(updatedUser);

        ResponseEntity<User> response = userController.updateUser(userId, updatedUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
    }

    @Test
    void getUser_ShouldReturnUser() throws UserNotFoundException {
        TestUser user = new TestUser();
        when(userService.getAuthenticatedUser()).thenReturn(user);

        ResponseEntity<Object> response = userController.getUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void getUser_ShouldReturnNotFound_WhenUserNotFound() throws UserNotFoundException {
        when(userService.getAuthenticatedUser()).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<Object> response = userController.getUser();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getUserById_ShouldReturnUser() {
        Long userId = 1L;
        TestUser user = new TestUser();
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = userController.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void getUserById_ShouldReturnNotFound_WhenUserNotFound() {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = userController.getUserById(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getProfilePicture_ShouldReturnProfilePicture() {
        String username = "testuser";
        byte[] profilePicture = new byte[] { 1, 2, 3 };
        when(authentication.getName()).thenReturn(username);
        when(userService.getProfilePictureByUsername(username)).thenReturn(Optional.of(profilePicture));

        ResponseEntity<Object> response = userController.getProfilePicture();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertArrayEquals(profilePicture, (byte[]) response.getBody());
    }

    @Test
    void getProfilePicture_ShouldReturnNotFound_WhenProfilePictureNotFound() {
        String username = "testuser";
        when(authentication.getName()).thenReturn(username);
        when(userService.getProfilePictureByUsername(username)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = userController.getProfilePicture();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createBooking_ShouldReturnCreatedBooking() {
        BookingDetails bookingDetails = new BookingDetails();
        Booking createdBooking = new Booking();
        when(userService.createBooking(bookingDetails)).thenReturn(createdBooking);

        ResponseEntity<?> response = userController.createBooking(bookingDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createdBooking, response.getBody());
    }

    @Test
    void logObjectAttributes_ShouldLogAttributes() {
        BookingDetails bookingDetails = new BookingDetails();
        UserController.logObjectAttributes(bookingDetails);
        // This test is mainly to ensure the method doesn't throw any exceptions
        // You might want to use a mocking framework to capture System.out.println
        // output if needed
    }
}