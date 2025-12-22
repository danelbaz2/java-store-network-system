# Phase 1: Protocol Layer

## What It Does
The Protocol layer defines the **communication language** between client and server. It's like a contract - both sides agree on how messages look.

## Files Created

### `ActionType.java`
An enum listing all possible actions:
- `LOGIN`, `LOGOUT` - Authentication
- `GET_INVENTORY`, `SELL_PRODUCT` - Store operations
- `GET_EMPLOYEES`, `ADD_EMPLOYEE`, etc. - Employee management
- `SEND_MESSAGE` - Chat

### `Request.java`
Message sent from **Client → Server**:
```java
Request req = new Request(ActionType.LOGIN, "BR01")
    .put("employeeId", "E001")
    .put("password", "Admin123!");
```

### `Response.java`
Message sent from **Server → Client**:
```java
Response.success("Login OK").put("sessionId", "abc123");
Response.error("Invalid password");
```

## Why Serializable?
Both classes implement `Serializable` so they can be sent over TCP sockets using `ObjectInputStream`/`ObjectOutputStream`.
