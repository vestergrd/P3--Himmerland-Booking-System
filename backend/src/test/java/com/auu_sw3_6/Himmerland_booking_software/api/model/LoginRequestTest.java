package com.auu_sw3_6.Himmerland_booking_software.api.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        String username = "testUser";
        String password = "testPassword";

        // Act
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        // Assert
        assertEquals(username, loginRequest.getUsername());
        assertEquals(password, loginRequest.getPassword());
    }

    @Test
    void setNullValues_ShouldHandleGracefully() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();

        // Act
        loginRequest.setUsername(null);
        loginRequest.setPassword(null);

        // Assert
        assertNull(loginRequest.getUsername());
        assertNull(loginRequest.getPassword());
    }

    @Test
    void defaultValues_ShouldBeNull() {
        // Act
        LoginRequest loginRequest = new LoginRequest();

        // Assert
        assertNull(loginRequest.getUsername());
        assertNull(loginRequest.getPassword());
    }
}
