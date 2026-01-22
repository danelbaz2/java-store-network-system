package com.storemgmt.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatAvailabilityObserver {

    private static final Map<String, List<String>> pendingChatRequests = new HashMap<>();

    // adding the request to the list
    public static synchronized void subscribe(String requesterEmpId, String targetEmpId) {
        List<String> requesters = pendingChatRequests.get(targetEmpId);
        if (requesters == null) {
            requesters = new ArrayList<>();
            pendingChatRequests.put(targetEmpId, requesters);
        }
        requesters.add(requesterEmpId);
    }

    // removing the request from the list
    public static synchronized void unsubscribe(String requesterEmpId) {
        for (List<String> requesters : pendingChatRequests.values()) {
            requesters.remove(requesterEmpId);
        }
        pendingChatRequests.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    // getting the list of waiting requesters and removing them from the list
    public static synchronized List<String> getAndClearWaitingRequesters(String targetEmpId) {
        List<String> waitingList = pendingChatRequests.remove(targetEmpId);
        return waitingList != null ? waitingList : new ArrayList<>();
    }

    // checking if the target employee has pending requests
    public static synchronized boolean hasPendingRequests(String targetEmpId) {
        List<String> waiters = pendingChatRequests.get(targetEmpId);
        return waiters != null && !waiters.isEmpty();
    }
}
