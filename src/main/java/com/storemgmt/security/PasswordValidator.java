package com.storemgmt.security;

/**
 * Simple password validator for the store management system.
 * Checks that passwords meet basic security requirements.
 */
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;

    public static String validate(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }

        if (password.length() < MIN_LENGTH) {
            return "Password must be at least " + MIN_LENGTH + " characters";
        }

        if (!hasUppercase(password)) {
            return "Password must contain at least one uppercase letter";
        }

        if (!hasLowercase(password)) {
            return "Password must contain at least one lowercase letter";
        }

        if (!hasDigit(password)) {
            return "Password must contain at least one digit";
        }

        return "valid";
    }

    private static boolean hasUppercase(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasLowercase(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasDigit(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValid(String password) {
        return validate(password).equals("valid");
    }
}
