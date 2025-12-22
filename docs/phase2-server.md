# Phase 2: Server Implementation

## What It Does
The Server is the **central brain** that:
- Listens for client connections
- Processes all requests (login, sales, inventory)
- Manages shared data (Chain with all branches)
- Ensures thread-safe access when multiple clients connect

## Files Created

### `StoreServer.java`
The main TCP server:
```
┌─────────────────────────────────────┐
│         StoreServer (port 5000)     │
│  • Accepts client connections       │
│  • Creates ClientHandler per client │
│  • Holds reference to Chain         │
└─────────────────────────────────────┘
```

### `ClientHandler.java`
One thread per connected client:
- Reads `Request` from client
- Routes to handler method (`handleLogin`, `handleSellProduct`, etc.)
- Sends `Response` back
- Uses `synchronized` for thread-safe Chain access

### `ServerApp.java`
Entry point that:
1. Creates Chain with sample data
2. Sets up 2 branches (Tel Aviv, Jerusalem)
3. Adds sample employees
4. Starts server on port 5000

## How to Run
```bash
mvn compile exec:java -Dexec.mainClass="com.storemgmt.server.ServerApp"
```

## Thread Safety
When multiple clients request data simultaneously, `synchronized` blocks prevent race conditions:
```java
synchronized (chain) {
    StoreBranch branch = chain.getBranchById(branchId);
    // Safe access
}
```
