package com.storemgmt.model.session;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserSession {
    private final String sessionId;
    private final String employeeId;
    private final String branchId;
    private final LocalDateTime loginTime;
    private boolean active;

    public UserSession(String employeeId, String branchId) {
        this.sessionId = UUID.randomUUID().toString();
        this.employeeId = employeeId;
        this.branchId = branchId;
        this.loginTime = LocalDateTime.now();
        this.active = true;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getBranchId() {
        return branchId;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public boolean isActive() {
        return active;
    }

    public void close() {
        this.active = false;
    }
}
