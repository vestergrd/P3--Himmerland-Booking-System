package com.auu_sw3_6.Himmerland_booking_software.api.model;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UserTest {

    private User user;
    private Validator validator;
    private static class ConcreteUser extends User {

        public ConcreteUser(long id, String name, String email, String mobileNumber, String username, String password,
                            String profilePictureFileName, String houseAddress) {
            super(id, name, email, mobileNumber, username, password, profilePictureFileName, houseAddress);
        }
    }

    @BeforeEach
    public void setUp() {
        user = new ConcreteUser(1L, "John Doe", "johndoe@example.com", "+1234567890", "johndoe", "password123",
                "profile.jpg", "123 Main St");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testConstructor_ShouldInitializeFields() {
        // Act & Assert
        assertNotNull(user);
        assertEquals(1L, user.getId(), "ID should be 1.");
        assertEquals("John Doe", user.getName(), "Name should be 'John Doe'.");
        assertEquals("johndoe@example.com", user.getEmail(), "Email should be 'johndoe@example.com'.");
        assertEquals("+1234567890", user.getMobileNumber(), "Mobile number should be '+1234567890'.");
        assertEquals("johndoe", user.getUsername(), "Username should be 'johndoe'.");
        assertEquals("password123", user.getPassword(), "Password should be 'password123'.");
        assertEquals("profile.jpg", user.getProfilePictureFileName(), "Profile picture file name should be 'profile.jpg'.");
    }

    @Test
    public void testSetters_ShouldUpdateFields() {
        // Act
        user.setName("Jane Smith");
        user.setEmail("janesmith@example.com");
        user.setMobileNumber("+9876543210");
        user.setUsername("janesmith");
        user.setPassword("newpassword456");
        user.setProfilePictureFileName("newprofile.jpg");

        // Assert
        assertEquals("Jane Smith", user.getName(), "Name should be updated to 'Jane Smith'.");
        assertEquals("janesmith@example.com", user.getEmail(), "Email should be updated to 'janesmith@example.com'.");
        assertEquals("+9876543210", user.getMobileNumber(), "Mobile number should be updated to '+9876543210'.");
        assertEquals("janesmith", user.getUsername(), "Username should be updated to 'janesmith'.");
        assertEquals("newpassword456", user.getPassword(), "Password should be updated to 'newpassword456'.");
        assertEquals("newprofile.jpg", user.getProfilePictureFileName(), "Profile picture file name should be updated to 'newprofile.jpg'.");
    }

    @Test
    public void testValidation_ValidUser() {
        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Assert
        assertTrue(violations.isEmpty(), "There should be no validation violations for a valid User.");
    }

    @Test
    public void testValidation_InvalidUser() {
        // Arrange
        user.setName("Jo");
        user.setEmail("invalid-email");
        user.setMobileNumber("123");
        user.setUsername("jd");
        user.setPassword("short");

        // Act
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Assert
        assertTrue(violations.size() > 0, "There should be validation violations for an invalid User.");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Name should have at least 3 characters")),
                "Violation should indicate that Name should have at least 3 characters.");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email should be valid")),
                "Violation should indicate that Email should be valid.");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Mobile number is invalid")),
                "Violation should indicate that Mobile number is invalid.");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Username should have at least 3 characters")),
                "Violation should indicate that Username should have at least 3 characters.");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password should have at least 8 characters")),
                "Violation should indicate that Password should have at least 8 characters.");
    }
}
