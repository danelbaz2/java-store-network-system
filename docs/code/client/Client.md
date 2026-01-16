# Client Classes

**Package:** `com.storemgmt.client`

## Overview

The client provides a console interface for branch employees to interact with the central server.

```
ClientApp (main)
    └── BranchClient (TCP connection to server)
            └── BranchMenu (console UI)
```

---

## ClientApp.java

### Purpose
- **Entry point** for the client application
- Takes branch ID as command-line argument

### Usage
```bash
java ClientApp BR01              # Connect as Branch 01
java ClientApp BR02 192.168.1.5  # Connect to remote server
```

---

## BranchClient.java

### Purpose
- Handles TCP communication with the server
- Sends requests, receives responses

### Key Methods

| Method | Description |
|--------|-------------|
| `connect()` | Establishes socket connection |
| `send(String msg)` | Sends request, returns response as String[] |
| `isConnected()` | Checks if socket is alive |
| `getBranchId()` | Returns this client's branch ID |

### send() Method
```java
public String[] send(String message) {
    1. Write message to output stream
    2. Read response line from server
    3. Split response by "|" delimiter
    4. Return as String array
}
```

### Response Parsing Helper
```java
public static HashMap<String, String> getData(String[] response) {
    // Parses "key:value" pairs from response
    // Example: ["OK", "Success", "name:John", "role:ADMIN"]
    // Returns: {name: "John", role: "ADMIN"}
}
```

---

## BranchMenu.java

### Purpose
- Console-based user interface
- Presents menu options, handles user input

### Menu Options

**Regular Employee:**
| # | Option |
|---|--------|
| 1 | View Inventory |
| 3 | View Employees |
| 4 | Send Chat |
| 5 | Logout |
| 0 | Exit |

**Admin (additional):**
| # | Option |
|---|--------|
| 6 | Add Employee |
| 7 | Remove Employee |
| 8 | Update Inventory |

> Note: Option 2 (Sell) is currently commented out in the menus.

### Flow

```
run()
    └── Loop while connected:
        1. login() → Authenticate user
        2. Print menu (based on role)
        3. Read user choice
        4. Execute selected action
        5. Repeat until logout or exit
```

### Key Methods

| Method | Description |
|--------|-------------|
| `login()` | Prompts for credentials, stores session |
| `viewInventory()` | Requests and displays stock |
| `sell()` | **Incomplete** - Collects product info, sends to server |
| `viewEmployees()` | Lists branch staff |
| `sendChat()` | Sends message (placeholder) |
| `logout()` | Clears session, notifies server |

### Session State
```java
private String sessionId;      // Token from login
private String employeeName;   // Logged-in user's name
private String role;           // "ADMIN" or "REGULAR"
```

---

## Example Session

```
Employee ID: E001
Password: Pass123!

Login successful
Welcome, John!

========================================
       Branch BR01 Menu
       Logged in: John (ADMIN)
========================================
1. View Inventory
3. View Employees
4. Send Chat
5. Logout
6. Add Employee
7. Remove Employee
8. Update Inventory
0. Exit
========================================
Choose option: 1

Inventory for Tel Aviv
-----------------------------------
P001 - NIKE Jean (50 units) - $150.0
P002 - BERSHKA Jean (50 units) - $150.0
...
```
