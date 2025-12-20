package com.storemgmt;

import com.storemgmt.model.Employee;
import com.storemgmt.model.Role;
import com.storemgmt.security.PasswordPolicy;
import com.storemgmt.service.AuthService;
import com.storemgmt.service.SessionManager;
import com.storemgmt.store.EmployeeStore;
import com.storemgmt.utils.ILogger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthServiceTest {

    // Simple test logger (no file IO during tests)
    private static class TestLogger implements ILogger {
        @Override public void info(String message) { }
        @Override public void error(String message) { }
        @Override public void error(String message, Exception ex) { }
    }

    @Test
    void loginWithCorrectCredentialsShouldCreateSession() {
    EmployeeStore store = new EmployeeStore("target/test-employees.csv");
        store.add(new Employee("1001", "100000001", "Anna", "Test User", "14004577", "BR01", Role.ADMIN, "Pass1234!"));

        AuthService auth = new AuthService(
                store,
                new SessionManager(),
                PasswordPolicy.defaultPolicy(),
                new TestLogger()
        );

        var session = auth.login("1001", "Pass1234!");
        assertNotNull(session);
        assertNotNull(session.getSessionId());
    }

    @Test
    void doubleLoginShouldFail() {
    EmployeeStore store = new EmployeeStore("target/test-employees.csv");
        store.add(new Employee("1001", "100000001", "Anna", "Test User", "14004577", "BR01", Role.ADMIN, "Pass1234!"));

        AuthService auth = new AuthService(
                store,
                new SessionManager(),
                PasswordPolicy.defaultPolicy(),
                new TestLogger()
        );

        auth.login("1001", "Pass1234!");

        assertThrows(IllegalStateException.class, () -> auth.login("1001", "Pass1234!"));
    }
}
