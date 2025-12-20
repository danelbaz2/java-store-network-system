# Client-Server Architecture Implementation Plan

## User Decisions

- ✅ **Replace** `ConsoleMenu.java` with new `BranchMenu.java`
- ✅ **JSON files** for data persistence (employees, inventory, customers)
- ✅ **Chat feature** will be implemented last

---

## Goal

Convert the store management system into a proper client-server architecture where:
- **Server**: Runs once, holds the Chain (central data), handles all business logic
- **Client**: Runs per branch (2 instances), provides UI for branch employees

---

## Current State

The project currently has:
- `Chain.java` - manages branches and customers (will be the server's core)
- `StoreBranch.java` - branch with inventory and sales
- `Employee.java`, `Role.java` - employee management
- `AuthService.java`, `SessionManager.java` - authentication
- `ConsoleMenu.java` - monolithic console UI (will be split)

---

## Proposed Changes

### Server Package

#### [NEW] StoreServer.java

Main server class that:
- Listens on a TCP port (default 5000)
- Accepts client connections
- Creates a new `ClientHandler` thread per connection
- Holds reference to the `Chain` instance

```java
public class StoreServer {
    private final Chain chain;
    private final int port;
    private ServerSocket serverSocket;
    
    public void start() {
        // Accept connections in loop
        // Create ClientHandler for each
    }
}
```

---

#### [NEW] ClientHandler.java

Handles one client connection (one branch):
- Runs in its own thread
- Reads `Request` objects from client
- Processes via `Chain` (synchronized)
- Sends `Response` back

```java
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Chain chain;
    private String branchId;
    
    public void run() {
        // Read requests, process, send responses
    }
}
```

---

#### [NEW] ServerApp.java

Entry point for the server:

```java
public class ServerApp {
    public static void main(String[] args) {
        Chain chain = new Chain("StoreChain");
        chain.addBranch(new StoreBranch("BR01", "Tel Aviv"));
        chain.addBranch(new StoreBranch("BR02", "Jerusalem"));
        
        StoreServer server = new StoreServer(chain, 5000);
        server.start();
    }
}
```

---

### Client Package

#### [NEW] BranchClient.java

Client that connects to server:
- Establishes TCP connection
- Sends `Request` objects
- Receives `Response` objects
- Handles connection lifecycle

```java
public class BranchClient {
    private final String host;
    private final int port;
    private final String branchId;
    private Socket socket;
    
    public void connect() { ... }
    public Response sendRequest(Request request) { ... }
    public void disconnect() { ... }
}
```

---

#### [NEW] BranchMenu.java

Console UI for branch employees (similar to current ConsoleMenu but as client):
- Login/logout
- View inventory
- Sell products
- Chat with other branches

---

#### [NEW] ClientApp.java

Entry point for each branch client:

```java
public class ClientApp {
    public static void main(String[] args) {
        String branchId = args[0];  // "BR01" or "BR02"
        String host = args.length > 1 ? args[1] : "localhost";
        int port = args.length > 2 ? Integer.parseInt(args[2]) : 5000;
        
        BranchClient client = new BranchClient(host, port, branchId);
        client.connect();
        
        new BranchMenu(client).run();
    }
}
```

---

### Protocol Package

#### [NEW] Request.java

Serializable request object:

```java
public class Request implements Serializable {
    private String action;      // "LOGIN", "SELL", "GET_INVENTORY", "CHAT", etc.
    private String branchId;
    private Map<String, String> data;
}
```

---

#### [NEW] Response.java

Serializable response object:

```java
public class Response implements Serializable {
    private boolean success;
    private String message;
    private Map<String, Object> data;
}
```

---

### Model Updates

#### [MODIFY] Chain.java

Add synchronized methods for thread-safe operations:

```java
public class Chain {
    // Existing fields...
    
    public synchronized StoreBranch getBranchById(String branchId) { ... }
    public synchronized boolean sellProduct(String branchId, String productId, int qty) { ... }
    public synchronized List<Product> getInventory(String branchId) { ... }
}
```

---

## Architecture Diagram

```
┌────────────────────────────────────────────────────────────┐
│                    SERVER (ServerApp)                       │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                    StoreServer                        │  │
│  │           Listens on port 5000                        │  │
│  └────────────────────────┬─────────────────────────────┘  │
│                           │                                 │
│  ┌────────────────────────▼─────────────────────────────┐  │
│  │                      Chain                            │  │
│  │  • StoreBranch BR01 (inventory, sales)               │  │
│  │  • StoreBranch BR02 (inventory, sales)               │  │
│  │  • All employees, customers                           │  │
│  │  • Synchronized methods for thread safety             │  │
│  └──────────────────────────────────────────────────────┘  │
│                  ▲                    ▲                     │
│                  │                    │                     │
│  ┌───────────────┴────┐    ┌─────────┴───────────────┐    │
│  │  ClientHandler #1  │    │   ClientHandler #2      │    │
│  │  (Thread for BR01) │    │   (Thread for BR02)     │    │
│  └───────────────┬────┘    └─────────┬───────────────┘    │
└──────────────────┼───────────────────┼─────────────────────┘
                   │                   │
         TCP Socket│                   │TCP Socket
                   │                   │
         ┌─────────▼────────┐ ┌────────▼─────────┐
         │ ClientApp BR01   │ │ ClientApp BR02   │
         │ ┌──────────────┐ │ │ ┌──────────────┐ │
         │ │ BranchClient │ │ │ │ BranchClient │ │
         │ └──────────────┘ │ │ └──────────────┘ │
         │ ┌──────────────┐ │ │ ┌──────────────┐ │
         │ │ BranchMenu   │ │ │ │ BranchMenu   │ │
         │ └──────────────┘ │ │ └──────────────┘ │
         └──────────────────┘ └──────────────────┘
              Terminal 2          Terminal 3
```

---

## New Files Summary

| Package | File | Purpose |
|---------|------|---------|
| `server` | `StoreServer.java` | TCP server, accepts connections |
| `server` | `ClientHandler.java` | Handles one client (thread) |
| `server` | `ServerApp.java` | Server entry point |
| `client` | `BranchClient.java` | TCP client, communicates with server |
| `client` | `BranchMenu.java` | Console UI for branch |
| `client` | `ClientApp.java` | Client entry point |
| `protocol` | `Request.java` | Request message format |
| `protocol` | `Response.java` | Response message format |

---

## Verification Plan

### Manual Testing

**Test 1: Basic Connection**
1. Terminal 1: `java -cp target/classes com.storemgmt.server.ServerApp`
2. Terminal 2: `java -cp target/classes com.storemgmt.client.ClientApp BR01`
3. Verify: Client connects and shows menu

**Test 2: Two Clients**
1. Start server (Terminal 1)
2. Start BR01 client (Terminal 2)
3. Start BR02 client (Terminal 3)
4. Verify: Both connected

**Test 3: Concurrent Sale**
1. Both clients try to buy 30 Nike Jeans
2. Verify: One succeeds, one fails with "Not enough stock"

---

## Implementation Priority

1. **Phase 1**: Server & Client core (connection, basic protocol)
2. **Phase 2**: Authentication (login/logout via server)
3. **Phase 3**: Inventory & Sales operations
4. **Phase 4**: JSON data persistence
5. **Phase 5**: Chat between branches (LAST)
