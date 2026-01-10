package com.storemgmt.chat;

/**
 * Represents a request for a chat.
 */
public class ChatRequest {

    private final String fromEmployeeId;
    private final String fromBranchId;

    public ChatRequest(String fromEmployeeId, String fromBranchId) {
        this.fromEmployeeId = fromEmployeeId;
        this.fromBranchId = fromBranchId;
    }

    public String getFromEmployeeId() {
        return fromEmployeeId;
    }

    public String getFromBranchId() {
        return fromBranchId;
    }
}
