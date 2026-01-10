package com.storemgmt.chat;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Manages waiting chat requests.
 */
public class ChatQueue {

    private final Queue<ChatRequest> queue = new LinkedList<>();

    public synchronized void add(ChatRequest request) {
        queue.add(request);
    }

    public synchronized ChatRequest poll() {
        return queue.poll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized int size() {
        return queue.size();
    }
}
