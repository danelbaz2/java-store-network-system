# ClientHandler

**Package:** `com.storemgmt.server`  
**Type:** Client Request Handler (Runnable)

## Purpose

Handles all communication with a single connected client. Processes commands, manages chat sessions, and coordinates with other handlers for inter-client features.

## Relationships

```
ClientHandler
    │
    ├──▶ Socket (client connection)
    ├──▶ Chain (shared data model)
    ├──▶ StoreServer (access to other clients)
    ├──▶ CustomerHandler (spawns for sales)
    └──◀▶ Other ClientHandlers (chat communication)
```

## Fields

| Field | Type | Description |
|-------|------|-------------|
| `socket` | Socket | Client connection |
| `chain` | Chain | Shared data model |
| `server` | StoreServer | Parent server reference |
| `in` / `out` | DataInputStream / PrintStream | I/O streams |
| `branchId` | String | Client's branch ID |
| `currentEmployee` | Employee | Logged-in employee |
| `isAvailableForChat` | boolean | Chat availability flag |
| `chatPartnerId` | String | Current chat partner's ID |
| `chatObservers` | Map<String, List<String>> | Managers observing chats |

## Protocol Commands

### Authentication
| Command | Handler | Description |
|---------|---------|-------------|
| `LOGIN` | `handleLogin()` | Authenticates employee |
| `LOGOUT` | `handleLogout()` | Logs out employee |

### Availability
| Command | Handler | Description |
|---------|---------|-------------|
| `SET_AVAILABLE` | direct | Makes employee available for chat |
| `SET_UNAVAILABLE` | direct | Makes employee unavailable |

### Data Queries
| Command | Handler | Description |
|---------|---------|-------------|
| `GET_INVENTORY` | `handleGetInventory()` | Returns branch products |
| `GET_EMPLOYEES` | `handleGetEmployees()` | Returns branch employees |
| `VIEW_CUSTOMERS` | `handleViewCustomers()` | Returns customer queue |
| `GET_OTHER_BRANCH_EMPLOYEES` | `handleGetOtherBranchEmployees()` | Returns available employees from other branches |

### Operations
| Command | Handler | Description |
|---------|---------|-------------|
| `SELL` | `handleSell()` | Starts sale via CustomerHandler |
| `ADD_EMPLOYEE` | `handleAddEmployee()` | Creates new employee (ADMIN) |
| `REMOVE_EMPLOYEE` | `handleRemoveEmployee()` | Deletes employee (ADMIN) |
| `UPDATE_INVENTORY` | `handleUpdateInventory()` | Modifies stock (MANAGER+) |

### Reports
| Command | Handler | Description |
|---------|---------|-------------|
| `SHOW_REPORT_BY_BRANCH` | `handleShowReport("branch")` | Sales by branch |
| `SHOW_REPORT_BY_CATEGORY` | `handleShowReport("category")` | Sales by category |
| `SHOW_REPORT_BY_PRODUCT` | `handleShowReport("product")` | Sales by product |

### Chat System
| Command | Handler | Description |
|---------|---------|-------------|
| `START_CHAT` | `handleStartChat()` | Initiates chat with another employee |
| `ACCEPT_CHAT` | `handleAcceptChat()` | Accepts incoming chat request |
| `END_CHAT` | `handleEndChat()` | Terminates chat session |
| `SEND_CHAT_MESSAGE` | `handleSendChatMessage()` | Sends message to chat partner |
| `GET_ACTIVE_CHATS` | `handleGetActiveChats()` | Lists active chats (MANAGER) |
| `JOIN_CHAT` | `handleJoinChat()` | Joins chat as observer (MANAGER) |
| `LEAVE_CHAT` | `handleLeaveChat()` | Leaves observed chat |
| `ADMIN_CHAT_MESSAGE` | `handleAdminChatMessage()` | Sends message to both participants |

## Key Functions

### `run()`
Main loop that reads commands, parses them, and routes to handlers.

### `handleSendChatMessage()`
Validates chat is still active before forwarding messages. Checks:
1. Sender's `chatPartnerId` matches target
2. Target's `chatPartnerId` matches sender

### `notifyWaitingEmployees()`
Called after employee finishes selling. Notifies employees who were waiting for this employee to become available.

### `sendChatMessage()` / `sendChatRequest()` / `sendChatEnd()`
Push notifications to client for chat events.

### `cleanup()`
Called on disconnect. Closes streams and removes from server's client list.

## Thread Safety Notes

- Uses `synchronized(chain)` for data modifications
- Chat state (`chatPartnerId`) accessed by multiple threads
- `chatObservers` map is synchronized for observer management
