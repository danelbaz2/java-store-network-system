package com.storemgmt.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Subject: The target employee being watched
//Observers: Employees waiting to chat with that target

public class ChatAvailabilityObserver {

    private static final Map<String, List<String>> pendingChatRequests = new HashMap<>();

    // requesterEmpId: The employee ID who wants to chat
    // targetEmpId: The employee ID they want to chat with
    public static synchronized void subscribe(String requesterEmpId, String targetEmpId) {
        pendingChatRequests
                .computeIfAbsent(targetEmpId, k -> new ArrayList<>())
                .add(requesterEmpId);
    }

    // requesterEmpId: The employee ID to remove from all subscriptions
    public static synchronized void unsubscribe(String requesterEmpId) {
        for (List<String> requesters : pendingChatRequests.values()) {
            requesters.remove(requesterEmpId);
        }
        // Clean up empty lists
        pendingChatRequests.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    // targetEmpId: The employee who just became available
    public static synchronized List<String> getAndClearWaitingRequesters(String targetEmpId) {
        List<String> waitingList = pendingChatRequests.remove(targetEmpId);
        return waitingList != null ? waitingList : new ArrayList<>();
    }

    // Check if anyone is waiting to chat with the target
    public static synchronized boolean hasPendingRequests(String targetEmpId) {
        List<String> waiters = pendingChatRequests.get(targetEmpId);
        return waiters != null && !waiters.isEmpty();
    }
}
