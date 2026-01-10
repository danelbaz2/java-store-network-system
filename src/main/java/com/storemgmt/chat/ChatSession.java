package com.storemgmt.chat;

import com.storemgmt.model.employee.Employee;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an active chat session between employees.
 */
public class ChatSession {

    private final String chatId;
    private final List<Employee> participants;

    public ChatSession(String chatId, Employee initiator, Employee target) {
        this.chatId = chatId;
        this.participants = new ArrayList<>();
        this.participants.add(initiator);
        this.participants.add(target);
    }

    public String getChatId() {
        return chatId;
    }

    public List<Employee> getParticipants() {
        return participants;
    }

    public boolean containsEmployee(String employeeId) {
        return participants.stream()
                .anyMatch(e -> e.getEmployeeId().equals(employeeId));
    }
}
