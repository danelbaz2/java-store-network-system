package com.storemgmt.chat;

public interface ChatObserver {
    void onQueueUpdate(ChatRequest nextRequest);
}