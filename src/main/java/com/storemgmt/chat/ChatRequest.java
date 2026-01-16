package com.storemgmt.chat;

import java.time.LocalDateTime;

public class ChatRequest {
    private String requesterId;    
    private LocalDateTime timestamp; 

    public ChatRequest(String requesterId) {
        this.requesterId = requesterId;
        this.timestamp = LocalDateTime.now();
    }

    public String getRequesterId() {
        return requesterId;
    }

    @Override
    public String toString() {
        return "Request from " + requesterId + " at " + timestamp;
    }
}