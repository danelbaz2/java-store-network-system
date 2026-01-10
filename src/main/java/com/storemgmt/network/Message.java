package com.storemgmt.network;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a message sent between client and server.
 * Each message has a type and an optional data map.
 */
public class Message implements Serializable {

    private final MessageType type;
    private final Map<String, Object> data;

    public Message(MessageType type) {
        this.type = type;
        this.data = new HashMap<>();
    }

    public MessageType getType() {
        return type;
    }

    public Message put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) data.get(key);
    }

    public Map<String, Object> getData() {
        return data;
    }
}
