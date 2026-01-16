# StoreServer

**Package:** `com.storemgmt.server`  
**Type:** TCP Server

## Purpose

Main TCP server that accepts client connections and spawns `ClientHandler` threads for each connected client.

## Relationships

```
StoreServer
    │
    ├──▶ Chain (shared data model)
    ├──▶ ServerSocket (accepts connections)
    └──▶ ClientHandler[] (one per connected client)
```

## Fields

| Field | Type | Description |
|-------|------|-------------|
| `chain` | Chain | Shared store data model |
| `port` | int | TCP port to listen on |
| `serverSocket` | ServerSocket | Main server socket |
| `running` | boolean | Server running status |
| `connectedClients` | ArrayList<ClientHandler> | All active client handlers |

## Functions

### `StoreServer(Chain chain, int port)`
- **Purpose:** Constructor
- **Parameters:**
  - `chain` - The shared Chain data model
  - `port` - Port number to listen on

### `start()`
- **Purpose:** Starts the server and accepts connections
- **Behavior:**
  - Opens `ServerSocket` on configured port
  - Loops accepting connections
  - For each connection:
    - Creates `ClientHandler`
    - Adds to `connectedClients`
    - Starts handler thread

### `stop()`
- **Purpose:** Gracefully shuts down the server
- **Behavior:** Closes server socket

### `removeClient(ClientHandler handler)`
- **Purpose:** Removes a disconnected client from the list
- **Called by:** `ClientHandler.cleanup()` when client disconnects

### `getConnectedClients()`
- **Purpose:** Returns list of all connected clients
- **Used for:** Inter-client communication (chat), finding other employees

### `getChain()`
- **Purpose:** Returns the shared Chain model
- **Used for:** Data access from handlers

## Thread Safety

- Each client runs in its own thread (`ClientHandler`)
- `connectedClients` list is accessed by multiple threads
- Shared `Chain` object requires synchronization for modifications
