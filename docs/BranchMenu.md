# BranchMenu

**Package:** `com.storemgmt.client`  
**Type:** User Interface Controller

## Purpose

Provides the command-line user interface for branch employees. Handles all user interactions, displays menus based on role, and processes user commands.

## Relationships

```
BranchMenu
    │
    ├──▶ BranchClient (network communication)
    ├──▶ JsonLoader (input validation)
    └──▶ PasswordValidator (password rules)
```

## Fields

| Field | Type | Description |
|-------|------|-------------|
| `client` | BranchClient | Network handler |
| `console` | DataInputStream | User input stream |
| `employeeName` | String | Current logged-in employee name |
| `role` | String | Current employee role (ADMIN/MANAGER/CASHIER/SELLER) |
| `pendingChatRequest` | boolean | Flag for incoming chat request |
| `pendingChatFromId` | String | ID of employee requesting chat |
| `chatEnded` | boolean | Flag when chat partner ends chat |

## Main Functions

### `run()`
- **Purpose:** Main loop that displays menu and processes commands
- **Behavior:** Shows different menus based on role, handles pending chat requests

### Menu Display Functions
| Function | Description |
|----------|-------------|
| `printRegularMenu()` | Basic menu for CASHIER/SELLER |
| `printManagerMenu()` | Extended menu with chat monitoring, inventory update |
| `printAdminMenu()` | Full menu with employee management, reports |

### Core Operations

| Function | Description | Role Required |
|----------|-------------|---------------|
| `login()` | Authenticates employee | None |
| `logout()` | Logs out current employee | Any |
| `viewInventory()` | Displays branch products | Any |
| `sell()` | Starts a sale transaction | Any |
| `viewEmployees()` | Lists branch employees | Any |
| `viewCustomers()` | Shows customer queue | Any |
| `startChat()` | Initiates chat with other branch | Any |
| `monitorChats()` | Observes active chats | MANAGER |
| `updateInventory()` | Modifies product stock | MANAGER, ADMIN |
| `addEmployee()` | Creates new employee | ADMIN |
| `removeEmployee()` | Deletes employee | ADMIN |
| `reportManagement()` | Generates sales reports | ADMIN |

### Chat Callback Functions

| Function | Description |
|----------|-------------|
| `onChatRequest()` | Handles incoming chat invitation |
| `onChatMessage()` | Displays received chat message |
| `onChatEnded()` | Notifies when partner ends chat |
| `onAvailabilityNotification()` | Notifies when waited employee becomes available |

### Input Validation Functions

| Function | Validation Rules |
|----------|-----------------|
| `readEmployeeId()` | Must start with 'E', must not exist |
| `readProductId()` | Must start with 'P', must exist in catalog |
| `readPhone()` | 10 digits, starts with '0' |
| `readBranchId()` | Must exist in config |
| `readRole()` | Must be valid Role enum |
| `readPassword()` | 8+ chars, uppercase, lowercase, digit |
| `readQuantity()` | Positive integer |

### Helper Functions
- `setAvailable(boolean)` - Sets employee availability for chat
- `printResponse(String[])` - Formats and displays server responses
- `isAllDigits(String)` / `isAllLetter(String)` - Input validation helpers
