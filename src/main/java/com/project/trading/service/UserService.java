package com.project.trading.service;

import com.project.trading.dao.UserDAO;
import com.project.trading.exception.AuthenticationException;
import com.project.trading.exception.DataAccessException;
import com.project.trading.exception.UserNotFoundException;
import com.project.trading.exception.ValidationException;
import com.project.trading.model.Role;
import com.project.trading.model.User;
import com.project.trading.model.UserStatus;
import com.project.trading.util.DBConnection;
import com.project.trading.util.PasswordUtil;
import com.project.trading.util.Validator;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User register(String username, String email, String fullName, String password) throws ValidationException {
        // 1. Validation
        Validator.requireNonBlank(username, "Username");
        Validator.matches(username, "^[A-Za-z0-9_]{3,30}$", "Username must be 3-30 characters (letters, numbers, underscores).");
        Validator.isValidEmail(email);
        Validator.requireNonBlank(fullName, "Full Name");
        if (fullName.length() > 100) {
            throw new ValidationException("Full Name must not exceed 100 characters.");
        }
        Validator.isValidPassword(password);

        try (Connection c = DBConnection.getConnection()) {
            // 2. Duplicate checks
            if (userDAO.existsByUsername(c, username)) {
                throw new ValidationException("Username already taken.");
            }
            if (userDAO.existsByEmail(c, email)) {
                throw new ValidationException("Email already registered.");
            }

            // 3. Hash password and insert
            String passwordHash = PasswordUtil.hash(password.toCharArray());
            User user = new User();
            user.setUsername(username.trim());
            user.setEmail(email.trim().toLowerCase());
            user.setFullName(fullName.trim());
            user.setPasswordHash(passwordHash);
            user.setRole(Role.USER);
            user.setStatus(UserStatus.ACTIVE);
            user.setCashBalance(new BigDecimal("100000.00"));

            long id = userDAO.insert(c, user);
            user.setId(id);
            return user;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new ValidationException("Username or email already exists.");
        } catch (SQLException e) {
            throw new DataAccessException("Registration failed due to a database error.", e);
        }
    }

    public User login(String username, String password) throws AuthenticationException {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new AuthenticationException("Invalid username or password.");
        }

        try (Connection c = DBConnection.getConnection()) {
            User user = userDAO.findByUsername(c, username.trim());
            if (user == null || !PasswordUtil.verify(password.toCharArray(), user.getPasswordHash())) {
                // Same message for unknown user and wrong password (prevents username enumeration)
                throw new AuthenticationException("Invalid username or password.");
            }

            if (user.getStatus() == UserStatus.BLOCKED) {
                throw new AuthenticationException("Account blocked.");
            }

            return user;
        } catch (SQLException e) {
            throw new DataAccessException("Login failed due to a database error.", e);
        }
    }

    public User getById(long id) throws UserNotFoundException {
        try (Connection c = DBConnection.getConnection()) {
            User user = userDAO.findById(c, id);
            if (user == null) {
                throw new UserNotFoundException("User not found with id: " + id);
            }
            return user;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch user by id.", e);
        }
    }

    public void updateProfile(long id, String fullName, String email) throws ValidationException, UserNotFoundException {
        Validator.requireNonBlank(fullName, "Full Name");
        if (fullName.length() > 100) {
            throw new ValidationException("Full Name must not exceed 100 characters.");
        }
        Validator.isValidEmail(email);

        try (Connection c = DBConnection.getConnection()) {
            User existing = userDAO.findById(c, id);
            if (existing == null) {
                throw new UserNotFoundException("User not found with id: " + id);
            }

            String normalizedEmail = email.trim().toLowerCase();
            if (!normalizedEmail.equalsIgnoreCase(existing.getEmail()) && userDAO.existsByEmail(c, normalizedEmail)) {
                throw new ValidationException("Email already registered.");
            }

            userDAO.updateProfile(c, id, fullName.trim(), normalizedEmail);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new ValidationException("Email already registered.");
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update profile.", e);
        }
    }

    public void changePassword(long id, String oldPw, String newPw) throws ValidationException, AuthenticationException, UserNotFoundException {
        Validator.requireNonBlank(oldPw, "Current Password");
        Validator.isValidPassword(newPw);

        try (Connection c = DBConnection.getConnection()) {
            User user = userDAO.findById(c, id);
            if (user == null) {
                throw new UserNotFoundException("User not found with id: " + id);
            }

            if (!PasswordUtil.verify(oldPw.toCharArray(), user.getPasswordHash())) {
                throw new AuthenticationException("Incorrect current password.");
            }

            String newHash = PasswordUtil.hash(newPw.toCharArray());
            userDAO.updatePasswordHash(c, id, newHash);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to change password.", e);
        }
    }

    public List<User> listAll() {
        try (Connection c = DBConnection.getConnection()) {
            return userDAO.findAll(c);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list users.", e);
        }
    }

    public void setBlocked(long adminId, long targetId, boolean blocked) throws ValidationException, UserNotFoundException {
        if (adminId == targetId) {
            throw new ValidationException("Cannot block your own account.");
        }

        try (Connection c = DBConnection.getConnection()) {
            User target = userDAO.findById(c, targetId);
            if (target == null) {
                throw new UserNotFoundException("Target user not found with id: " + targetId);
            }

            if (target.getRole() == Role.ADMIN) {
                throw new ValidationException("Cannot block an admin account.");
            }

            UserStatus newStatus = blocked ? UserStatus.BLOCKED : UserStatus.ACTIVE;
            userDAO.updateStatus(c, targetId, newStatus);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update user status.", e);
        }
    }
}
