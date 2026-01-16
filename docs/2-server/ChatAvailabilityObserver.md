# ChatAvailabilityObserver

**Package:** `com.storemgmt.server`  
**Type:** Observer Pattern Implementation

## Purpose

Implements the Observer pattern for chat availability notifications. When an employee tries to chat with someone who is busy, they can subscribe to be notified when that employee becomes available.

## Relationships

```
ChatAvailabilityObserver (static utility)
    │
    └──▶ Used by ClientHandler for:
         - subscribe() when target is busy
         - getAndClearWaitingRequesters() when employee finishes task
```

## Data Structure

```java
Map<String, List<String>> pendingChatRequests
// Key: targetEmployeeId (the busy employee)
// Value: List of requesterEmployeeIds (employees waiting for them)
```

## Functions

### `subscribe(String requesterEmpId, String targetEmpId)`
- **Purpose:** Registers a requester to be notified when target becomes available
- **Parameters:**
  - `requesterEmpId` - Employee who wants to chat
  - `targetEmpId` - Employee who is currently busy
- **Thread Safety:** `synchronized`

### `unsubscribe(String requesterEmpId)`
- **Purpose:** Removes a requester from all waiting lists
- **Use Case:** When requester disconnects or cancels waiting
- **Thread Safety:** `synchronized`

### `getAndClearWaitingRequesters(String targetEmpId)`
- **Purpose:** Gets and removes all employees waiting for this target
- **Parameters:** `targetEmpId` - The employee who just became available
- **Returns:** List of employee IDs to notify
- **Thread Safety:** `synchronized`
- **Atomic:** Returns and clears in one operation

### `hasPendingRequests(String targetEmpId)`
- **Purpose:** Checks if anyone is waiting for this employee
- **Returns:** `true` if there are waiters
- **Thread Safety:** `synchronized`

## Usage Flow

1. **Employee A** tries to chat with **Employee B** (busy)
2. `ClientHandler.handleStartChat()` calls `subscribe("A", "B")`
3. Returns `"PENDING|Employee B is busy..."`
4. Later, **Employee B** finishes selling
5. `CustomerHandler.run()` calls `clientHandler.notifyWaitingEmployees()`
6. `notifyWaitingEmployees()` calls `getAndClearWaitingRequesters("B")`
7. **Employee A** receives `CHAT_AVAILABLE|B` notification

## Thread Safety

All methods are `synchronized` because:
- Multiple `ClientHandler` threads may subscribe simultaneously
- `CustomerHandler` threads may trigger notifications
- Prevents race conditions in the shared map
