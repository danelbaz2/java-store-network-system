package com.storemgmt.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatManager {

    // Singleton Implementation
    private static ChatManager instance;

    private ChatManager() {}

    public static synchronized ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }

    // Data Structures
    private Queue<ChatRequest> waitingQueue = new ConcurrentLinkedQueue<>();

    private List<ChatObserver> observers = new ArrayList<>();


    // Queue Management Methods
    
    public void addRequest(String requesterId) {
        ChatRequest req = new ChatRequest(requesterId);
        waitingQueue.offer(req); 
        System.out.println("[ChatManager] Added request to queue: " + requesterId);
        
        notifyObservers();
    }

    public ChatRequest pullNextRequest() {
        return waitingQueue.poll(); 
    }
    
    public boolean hasRequests() {
        return !waitingQueue.isEmpty();
    }


    // Observer Pattern Implementation

    public synchronized void registerObserver(ChatObserver o) {
        observers.add(o);
    }

    public synchronized void removeObserver(ChatObserver o) {
        observers.remove(o);
    }

    public synchronized void notifyObservers() {
        if (waitingQueue.isEmpty()) return;

        ChatRequest nextReq = waitingQueue.peek();

        for (ChatObserver o : observers) {
            o.onQueueUpdate(nextReq);
        }
    }
}