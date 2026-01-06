package com.storemgmt.service;

import com.storemgmt.model.employee.Employee;
import com.storemgmt.model.session.UserSession;
import com.storemgmt.security.PasswordValidator;
import com.storemgmt.store.EmployeeStore;
import com.storemgmt.utils.ILogger;

public class AuthService {

    private final EmployeeStore employeeStore;
    private final SessionManager sessionManager;
    private final ILogger logger;

    public AuthService(EmployeeStore employeeStore, SessionManager sessionManager, ILogger logger) {
        this.employeeStore = employeeStore;
        this.sessionManager = sessionManager;
        this.logger = logger;
    }

    public UserSession login(String employeeId, String password) {
        Employee employee = employeeStore.findById(employeeId);
        if (employee == null) {
            throw new IllegalArgumentException("Employee not found");
        }

        // Validate password format
        String validationError = PasswordValidator.validate(password);
        if (validationError != null) {
            throw new IllegalArgumentException("Password policy failed: " + validationError);
        }

        // Check password matches (plain text for now)
        if (!employee.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        UserSession session = sessionManager.createSession(employee.getEmployeeId(), employee.getBranchId());
        logger.info("Login success. employeeId=" + employeeId + ", sessionId=" + session.getSessionId());
        return session;
    }

    public void logout(String sessionId) {
        sessionManager.closeSession(sessionId);
        logger.info("Logout. sessionId=" + sessionId);
    }
}
