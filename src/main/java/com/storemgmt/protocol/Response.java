package com.storemgmt.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Response object sent from Server to Client over TCP.
 * 
 * Each response contains:
 * - success: Whether the operation succeeded
 * - message: Human-readable result message (error message or success
 * confirmation)
 * - data: A map of response data (e.g., product list, session info)
 * 
 * Example usage:
 * // Success response
 * Response.success("Login successful")
 * .put("sessionId", "abc-123")
 * .put("role", "ADMIN");
 * 
 * // Error response
 * Response.error("Invalid credentials");
 */
public class Response {

    private final boolean success; // Did the operation succeed?
    private final String message; // Human-readable message
    private final Map<String, Object> data; // Response payload (can hold any object)

    // Private constructor - use static factory methods instead.
    private Response(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = new HashMap<>();
    }

    // Create a success response with a message.
    public static Response success(String message) {
        return new Response(true, message);
    }

    // Create a success response with default message.
    public static Response success() {
        return new Response(true, "Operation completed successfully");
    }

    // Create an error response with a message.
    public static Response error(String message) {
        return new Response(false, message);
    }

    // Cast method

    public static Response cast(String line) {
        if (line == null || !line.startsWith("Response{")) {
            return Response.error("Invalid response format: " + line);
        }

        try {
            // Basic parsing of "Response{success=..., message='...', ...}"
            boolean success = line.contains("success=true");

            String message = "";
            int msgStart = line.indexOf("message='");
            if (msgStart != -1) {
                int msgEnd = line.indexOf("'", msgStart + 9);
                if (msgEnd != -1) {
                    message = line.substring(msgStart + 9, msgEnd);
                }
            }

            return new Response(success, message);

        } catch (Exception e) {
            return Response.error("Parse error: " + e.getMessage());
        }
    }

    // ===== Builder Methods =====

    // Add data to the response. Allows chaining.
    public Response put(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    // ===== Getters =====

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // Get a String value from data.
    public String getString(String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Response{success=" + success + ", message='" + message + "', data=" + data + "}";
    }
}
