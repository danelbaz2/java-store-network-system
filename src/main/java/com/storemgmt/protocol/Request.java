package com.storemgmt.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Request object sent from Client to Server over TCP.
 * 
 * This class is Serializable so it can be sent through ObjectOutputStream.
 * Each request contains:
 * - action: What operation the client wants (LOGIN, SELL_PRODUCT, etc.)
 * - branchId: Which branch is making the request
 * - sessionId: The logged-in user's session (null before login)
 * - data: A map of parameters specific to the action
 * 
 * Example usage:
 * Request loginReq = new Request(ActionType.LOGIN, "BR01")
 * .put("employeeId", "E001")
 * .put("password", "Admin123!");
 */

public class Request {

    private final ActionType action; // What operation to perform
    private final String branchId; // Which branch is sending this request
    private String sessionId; // Session ID after login (null before login)
    private final Map<String, String> data; // Request parameters (employeeId, password, etc.)

    // Create a new request with the specified action and branch ID.
    public Request(ActionType action, String branchId) {
        this.action = action;
        this.branchId = branchId;
        this.data = new HashMap<>();
    }

    // Builder method to add a parameter to the request.
    // Allows chaining: request.put("key1", "value1").put("key2", "value2")
    public Request put(String key, String value) {
        this.data.put(key, value);
        return this;
    }

    // Getters
    public ActionType getAction() {
        return action;
    }

    public String getBranchId() {
        return branchId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    // Get a parameter value from the data map.
    public String get(String key) {
        return data.get(key);
    }

    // Get a parameter with a default value if not found.
    public String getOrDefault(String key, String defaultValue) {
        return data.getOrDefault(key, defaultValue);
    }

    public Map<String, String> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Request{action=" + action + ", branchId=" + branchId + ", data=" + data + "}";
    }

    /**
     * Parse a string representation of a Request (from toString).
     * Mocks a 'cast' from String to Request.
     */
    public static Request cast(String line) {
        try {
            if (line == null || !line.startsWith("Request{")) {
                return null;
            }

            // Extract action
            String actionStr = getValueBetween(line, "action=", ",");
            ActionType action = ActionType.valueOf(actionStr);

            // Extract branchId
            String branchId = getValueBetween(line, "branchId=", ",");

            Request req = new Request(action, branchId);

            // Extract data {key=value, ...}
            String dataStr = getValueBetween(line, "data={", "}");
            if (dataStr != null && !dataStr.isEmpty()) {
                // Remove potential outer braces if logic added them, typical toString is {k=v,
                // k=v}
                // dataStr is strictly content inside {}

                // Simple split by comma - NOTE: this fails if values contain commas
                String[] pairs = dataStr.split(", ");
                for (String pair : pairs) {
                    String[] kv = pair.split("=");
                    if (kv.length >= 2) {
                        // Join back value if split by = happened (unlikely for simple keys)
                        // But simple approach:
                        req.put(kv[0], kv[1]);
                    }
                }
            }

            return req;
        } catch (Exception e) {
            // System.err.println("Error parsing request: " + e.getMessage());
            return null;
        }
    }

    private static String getValueBetween(String text, String start, String end) {
        int s = text.indexOf(start);
        if (s == -1)
            return null;
        s += start.length();
        int e = text.indexOf(end, s);
        if (e == -1)
            return null;
        return text.substring(s, e);
    }
}
