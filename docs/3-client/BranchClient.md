# BranchClient

**Package:** `com.storemgmt.client`  
**Type:** Network Handler

## Purpose

Manages the TCP socket connection to the server and handles bidirectional communication. Runs a listener thread to receive asynchronous messages (like chat notifications) while also supporting synchronous request-response patterns.

## Relationships

```
BranchClient
    │
    ├──▶ Socket (TCP connection to server)
    ├──▶ BranchMenu (callback for chat events)
    └──◀ ClientHandler (server-side counterpart)
```

## Fields

| Field | Type | Description |
|-------|------|-------------|
| `host` | String | Server hostname |
| `port` | int | Server port number |
| `branchId` | String | Current branch identifier |
| `employeeId` | String | Logged-in employee ID |
| `socket` | Socket | TCP connection |
| `in` / `out` | DataInputStream / PrintStream | I/O streams |
| `connected` | boolean | Connection status |
| `pendingResponse` | String | Response waiting for processing |
| `responseLock` | Object | Synchronization lock for responses |
| `listenerThread` | Thread | Background message receiver |
| `inChatMode` | boolean | Whether user is in a chat session |
| `chatPartnerId` | String | ID of current chat partner |
| `menu` | BranchMenu | Reference for chat callbacks |

## Functions

### `connect()`
- **Purpose:** Establishes connection to server and starts listener thread
- **Returns:** `true` if connected successfully
- **Behavior:**
  - Creates socket connection
  - Starts background thread that:
    - Listens for incoming messages
    - Routes chat messages to `BranchMenu` callbacks
    - Routes command responses to `pendingResponse`

### `send(String message)`
- **Purpose:** Sends a command to server and waits for response
- **Parameters:** `message` - Protocol message (e.g., `"LOGIN|B001|employeeId:E001|password:..."`)
- **Returns:** `String[]` - Parsed response parts
- **Synchronization:** Uses `responseLock` to wait for response (30s timeout)

### `getData(String[] parts)` (static)
- **Purpose:** Parses key:value pairs from response
- **Parameters:** `parts` - Response array from `send()`
- **Returns:** `HashMap<String, String>` of extracted data

### `disconnect()`
- **Purpose:** Closes connection and cleans up resources

### Setters/Getters
- `setMenu()` / `setEmployeeId()` / `setChatPartnerId()` / `setInChatMode()`
- `getBranchId()` / `getEmployeeId()` / `getChatPartnerId()` / `isInChatMode()` / `isConnected()`

## Message Routing

The listener thread routes messages based on prefix:

| Prefix | Handler |
|--------|---------|
| `CHAT_REQUEST\|` | `menu.onChatRequest()` |
| `CHAT_MESSAGE\|` | `menu.onChatMessage()` |
| `CHAT_END\|` | `menu.onChatEnded()` |
| `CHAT_AVAILABLE\|` | `menu.onAvailabilityNotification()` |
| Other | Regular response (stored in `pendingResponse`) |
