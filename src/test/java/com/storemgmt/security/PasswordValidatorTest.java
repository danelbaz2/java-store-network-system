package com.storemgmt.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordValidatorTest {

    @Test
    void testValidPassword() {
        String result = PasswordValidator.validate("Password1");
        assertEquals("valid", result);
    }

    @Test
    void testShortPassword() {
        String result = PasswordValidator.validate("Pass1");
        assertTrue(result.contains("8"));
    }

    @Test
    void testNoUppercase() {
        String result = PasswordValidator.validate("password123");
        assertTrue(result.contains("uppercase"));
    }

    @Test
    void testNoDigit() {
        String result = PasswordValidator.validate("Password");
        assertTrue(result.contains("digit"));
    }

    @Test
    void testEmptyPassword() {
        String result = PasswordValidator.validate("");
        assertTrue(result.contains("empty"));
    }
}
