package com.storemgmt;

import com.storemgmt.security.PasswordPolicy;
import com.storemgmt.security.PasswordValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordValidatorTest {

    @Test
    void weakPasswordShouldFail() {
        PasswordPolicy policy = PasswordPolicy.defaultPolicy();
        var result = PasswordValidator.validate("abc", policy);
        assertFalse(result.isValid());
    }

    @Test
    void strongPasswordShouldPass() {
        PasswordPolicy policy = PasswordPolicy.defaultPolicy();
        var result = PasswordValidator.validate("Pass1234!", policy);
        assertTrue(result.isValid());
    }
}
