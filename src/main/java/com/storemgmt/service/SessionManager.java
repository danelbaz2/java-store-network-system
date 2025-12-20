package com.storemgmt.service;

import com.storemgmt.model.session.UserSession;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private final Map<String, UserSession> sessionsById = new HashMap<>();
    private final Map<String, String> activeSessionIdByEmployeeId = new HashMap<>();

    public UserSession createSession(String employeeId, String branchId) {
        if (activeSessionIdByEmployeeId.containsKey(employeeId)) {
            throw new IllegalStateException("Employee is already logged in");
        }

        UserSession session = new UserSession(employeeId, branchId);
        sessionsById.put(session.getSessionId(), session);
        activeSessionIdByEmployeeId.put(employeeId, session.getSessionId());
        return session;
    }

    public void closeSession(String sessionId) {
        UserSession session = sessionsById.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found");
        }

        if (session.isActive()) {
            session.close();
        }

        sessionsById.remove(sessionId);
        activeSessionIdByEmployeeId.remove(session.getEmployeeId());
    }

    public boolean isEmployeeLoggedIn(String employeeId) {
        return activeSessionIdByEmployeeId.containsKey(employeeId);
    }

    public UserSession getSession(String sessionId) {
        return sessionsById.get(sessionId);
    }
}
