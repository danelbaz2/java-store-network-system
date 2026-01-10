package com.storemgmt.chat;

import com.storemgmt.model.employee.Employee;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages active chat sessions.
 */
public class ChatManager {

    private final Map<String, ChatSession> activeChats = new HashMap<>();

    public ChatSession openChat(Employee from, Employee to) {
        String chatId = UUID.randomUUID().toString();
        ChatSession session = new ChatSession(chatId, from, to);
        activeChats.put(chatId, session);
        return session;
    }

    public ChatSession getChatById(String chatId) {
        return activeChats.get(chatId);
    }
}
