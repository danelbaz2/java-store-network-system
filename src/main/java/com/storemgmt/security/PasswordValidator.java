package com.storemgmt.security;

public class PasswordValidator {

    public static ValidationResult validate(String password, PasswordPolicy policy) {
        if (password == null) return ValidationResult.fail("Password is null");
        if (password.length() < policy.getMinLength()) {
            return ValidationResult.fail("Password must be at least " + policy.getMinLength() + " characters");
        }

        if (policy.isRequireUpper() && !password.matches(".*[A-Z].*")) {
            return ValidationResult.fail("Password must contain an uppercase letter");
        }
        if (policy.isRequireLower() && !password.matches(".*[a-z].*")) {
            return ValidationResult.fail("Password must contain a lowercase letter");
        }
        if (policy.isRequireDigit() && !password.matches(".*\\d.*")) {
            return ValidationResult.fail("Password must contain a digit");
        }
        if (policy.isRequireSpecial() && !password.matches(".*[^a-zA-Z0-9].*")) {
            return ValidationResult.fail("Password must contain a special character");
        }

        return ValidationResult.ok();
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult ok() { return new ValidationResult(true, "OK"); }
        public static ValidationResult fail(String message) { return new ValidationResult(false, message); }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
}
