# Server Classes

**Package:** `com.storemgmt.server`

## Overview

The server handles multiple branch clients connecting via TCP sockets.

```
ServerApp (main)
    └── StoreServer (listens on port 5000)
            └── ClientHandler (one per connected client, runs in thread)
                    └── Uses Chain for all business logic
```

---

## ServerApp.java

### Purpose
- **Entry point** for the server application
- Initializes the Chain with branches and starts listening

### What it does
1. Creates a `Chain` with branches (BR01, BR02)
2. Loads employees from file
3. Starts `StoreServer` on port 5000

---

## StoreServer.java

### Purpose
- TCP server that accepts client connections
- Creates a new `ClientHandler` thread for each client

### Key Methods

| Method | Description |
|--------|-------------|
| `start()` | Starts listening, accepts connections in loop |
| `removeClient(handler)` | Removes disconnected client from list |

### Connection Loop
```java
while (running) {
    Socket socket = serverSocket.accept();  // Blocks until client connects
    ClientHandler handler = new ClientHandler(socket, chain, this);
    new Thread(handler).start();  // Each client runs in own thread
}
```

---

## ClientHandler.java

### Purpose
- Handles one client connection (one branch)
- Parses requests, executes actions, sends responses

### Protocol Format
```
REQUEST:  ACTION|branchId|key1:value1|key2:value2
RESPONSE: OK|message|data:value   OR   ERROR|message
```

### Supported Actions

| Action | Description | Required Data |
|--------|-------------|---------------|
| `LOGIN` | Authenticate employee | employeeId, password |
| `LOGOUT` | End session | sessionId |
| `GET_INVENTORY` | View branch stock | (none) |
| `SELL_PRODUCT` | Process sale | productType, brand, quantity |
| `GET_EMPLOYEES` | List branch staff | sessionId |
| `SEND_MESSAGE` | Chat (placeholder) | message |
| `ADD_EMPLOYEE` | Admin: add staff | All employee fields |
| `REMOVE_EMPLOYEE` | Admin: remove staff | employeeId |
| `UPDATE_INVENTORY` | Admin: change stock | productId, quantity |

### Request/Response Examples

**Login:**
```
→ LOGIN|BR01|employeeId:E001|password:Pass123!
← OK|Login successful|sessionId:SESSION-123|employeeName:John|role:ADMIN
```

**Get Inventory:**
```
→ GET_INVENTORY|BR01|sessionId:SESSION-123
← OK|Inventory for Tel Aviv|inventory:P001 - NIKE Jean (50 units)...
```

### handleSellProduct() - Current State

```java
// ⚠️ Currently a STUB - just returns confirmation
// Does NOT actually update inventory yet
return "OK|Sale recorded: " + type + " - " + brand + " x" + qty;
```

> This needs to be connected to `StoreBranch.sell()` to work properly.

---

## Thread Safety

- `ClientHandler` uses `synchronized(chain)` blocks for:
  - Login (reading employees)
  - Inventory operations
  - All write operations

```java
synchronized (chain) {
    StoreBranch branch = chain.getBranchById(branchId);
    // ... safe operations here
}
```
