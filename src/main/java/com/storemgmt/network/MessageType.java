package com.storemgmt.network;

/**
 * Defines all message types exchanged between client and server.
 * This enum is gradually extended as new features are added.
 */
public enum MessageType {

    // Authentication
    LOGIN_REQUEST,
    LOGIN_RESPONSE,
    LOGOUT_REQUEST,

    // Basic chat (stage a)
    OPEN_CHAT,
    CHAT_REQUEST,
    CHAT_STARTED,
    CHAT_MESSAGE,
    CHAT_END,

    // General messages
    INFO,
    ERROR
}
