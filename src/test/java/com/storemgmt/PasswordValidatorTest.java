package com.storemgmt;

import com.storemgmt.security.PasswordValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordValidatorTest {

    @Test
    void weakPasswordShouldFail() {
        // Too short password
        String error = PasswordValidator.validate("abc");
        assertNotNull(error);
    }

    @Test
    void strongPasswordShouldPass() {
        // Valid password with uppercase, lowercase, and digit
        String error = PasswordValidator.validate("Pass1234");
        assertNull(error);
    }

    @Test
    void passwordWithoutUppercaseShouldFail() {
        String error = PasswordValidator.validate("password123");
        assertNotNull(error);
        assertTrue(error.contains("uppercase"));
    }

    @Test
    void passwordWithoutDigitShouldFail() {
        String error = PasswordValidator.validate("Password");
        assertNotNull(error);
        assertTrue(error.contains("digit"));
    }
}
