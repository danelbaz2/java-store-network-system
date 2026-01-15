package com.storemgmt.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatAvailabilityObserver {

    private static final Map<String, List<String>> pendingChatRequests = new HashMap<>();

    public static synchronized void subscribe(String requesterEmpId, String targetEmpId) {
        pendingChatRequests
                .computeIfAbsent(targetEmpId, k -> new ArrayList<>())
                .add(requesterEmpId);
    }

    public static synchronized void unsubscribe(String requesterEmpId) {
        for (List<String> requesters : pendingChatRequests.values()) {
            requesters.remove(requesterEmpId);
        }
        pendingChatRequests.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    public static synchronized List<String> getAndClearWaitingRequesters(String targetEmpId) {
        List<String> waitingList = pendingChatRequests.remove(targetEmpId);
        return waitingList != null ? waitingList : new ArrayList<>();
    }

    public static synchronized boolean hasPendingRequests(String targetEmpId) {
        List<String> waiters = pendingChatRequests.get(targetEmpId);
        return waiters != null && !waiters.isEmpty();
    }
}
