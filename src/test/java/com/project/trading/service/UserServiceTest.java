package com.project.trading.service;

import com.project.trading.exception.AuthenticationException;
import com.project.trading.exception.ValidationException;
import com.project.trading.model.User;
import com.project.trading.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    public void setup() throws Exception {
        com.project.trading.DBCreator.main(null);
        userService = new UserService();
    }

    @Test
    public void testRegistration_Success() {
        assertDoesNotThrow(() -> {
            User user = userService.register("newuser", "newuser@example.com", "New User", "Password123!");
            assertNotNull(user);
            assertEquals("newuser", user.getUsername());
            assertEquals("newuser@example.com", user.getEmail());
            assertEquals("New User", user.getFullName());
            assertEquals(new BigDecimal("100000.00"), user.getCashBalance(), "Initial cash should be exactly 100000.00");
        });
    }

    @Test
    public void testRegistration_DuplicateUsername() throws Exception {
        userService.register("user2", "user2@example.com", "User Two", "Password123!");
        assertThrows(ValidationException.class, () -> {
            userService.register("user2", "another@example.com", "User Two", "Password123!");
        }, "Should reject duplicate username");
    }

    @Test
    public void testRegistration_DuplicateEmail() throws Exception {
        userService.register("anotheruser", "user3@example.com", "User Three", "Password123!");
        assertThrows(ValidationException.class, () -> {
            userService.register("yetanother", "user3@example.com", "User Three", "Password123!");
        }, "Should reject duplicate email");
    }

    @Test
    public void testRegistration_InvalidData() {
        assertThrows(ValidationException.class, () -> {
            userService.register("a", "valid@example.com", "A", "Password123!"); // username too short
        });
        assertThrows(ValidationException.class, () -> {
            userService.register("validuser", "invalidemail", "Valid", "Password123!"); // invalid email
        });
        assertThrows(ValidationException.class, () -> {
            userService.register("validuser2", "valid2@example.com", "Valid", "weak"); // weak password
        });
    }

    @Test
    public void testProfileUpdate_Success() throws Exception {
        User user = userService.register("profileuser", "profile@example.com", "Profile User", "Password123!");
        assertDoesNotThrow(() -> {
            userService.updateProfile(user.getId(), "Updated Name", "updated@example.com");
        });
        
        User updated = userService.getById(user.getId());
        assertEquals("Updated Name", updated.getFullName());
        assertEquals("updated@example.com", updated.getEmail());
    }

    @Test
    public void testProfileUpdate_DuplicateEmail() throws Exception {
        User user1 = userService.register("user_a", "usera@example.com", "User A", "Password123!");
        User user2 = userService.register("user_b", "userb@example.com", "User B", "Password123!");
        
        assertThrows(ValidationException.class, () -> {
            userService.updateProfile(user2.getId(), "User B", "usera@example.com");
        });
    }

    @Test
    public void testPasswordChange_SuccessAndAuth() throws Exception {
        User user = userService.register("pwduser", "pwd@example.com", "Pwd User", "OldPassword123!");
        
        assertDoesNotThrow(() -> {
            userService.changePassword(user.getId(), "OldPassword123!", "NewPassword123!");
        });
        
        // Authenticate with new password should succeed
        assertDoesNotThrow(() -> {
            User loggedIn = userService.login("pwduser", "NewPassword123!");
            assertNotNull(loggedIn);
            assertTrue(PasswordUtil.verify("NewPassword123!".toCharArray(), loggedIn.getPasswordHash()));
        });
        
        // Authenticate with old password should fail
        assertThrows(AuthenticationException.class, () -> {
            userService.login("pwduser", "OldPassword123!");
        });
    }

    @Test
    public void testPasswordChange_IncorrectCurrentPassword() throws Exception {
        User user = userService.register("pwduser2", "pwd2@example.com", "Pwd User 2", "OldPassword123!");
        
        assertThrows(AuthenticationException.class, () -> {
            userService.changePassword(user.getId(), "WrongOldPassword!", "NewPassword123!");
        });
    }
}
