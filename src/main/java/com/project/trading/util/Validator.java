package com.project.trading.util;

import com.project.trading.exception.ValidationException;

import java.util.regex.Pattern;

public class Validator {

    public static void requireNonBlank(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required.");
        }
    }

    public static void matches(String value, String regex, String message) throws ValidationException {
        if (value == null || !Pattern.matches(regex, value)) {
            throw new ValidationException(message);
        }
    }

    public static void isValidEmail(String email) throws ValidationException {
        requireNonBlank(email, "Email");
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        matches(email, emailRegex, "Invalid email format.");
    }

    public static void isValidPassword(String password) throws ValidationException {
        requireNonBlank(password, "Password");
        if (password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long.");
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        if (!hasLetter || !hasDigit) {
            throw new ValidationException("Password must contain at least one letter and one digit.");
        }
    }

    public static int parsePositiveInt(String value, String fieldName) throws ValidationException {
        requireNonBlank(value, fieldName);
        try {
            int num = Integer.parseInt(value.trim());
            if (num <= 0) {
                throw new ValidationException(fieldName + " must be positive.");
            }
            return num;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid integer.");
        }
    }
}
