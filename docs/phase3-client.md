# Phase 3: Client Implementation

## What It Does
The Client is what **branch employees use**. Each branch runs its own client that:
- Connects to the central server
- Sends requests (login, view inventory, sell products)
- Displays responses to the user

## Files to Create

### `BranchClient.java`
Handles TCP connection to server:
```
┌─────────────────────────────────────┐
│           BranchClient              │
│  • connect(host, port)              │
│  • sendRequest(Request) → Response  │
│  • disconnect()                     │
└─────────────────────────────────────┘
```

### `BranchMenu.java`
Console UI that the employee sees:
```
========= Branch BR01 Menu =========
1. Login
2. View Inventory
3. Sell Product
4. Chat
5. Logout
0. Exit
====================================
```

### `ClientApp.java`
Entry point for each branch:
```bash
# Run Branch 1 (Tel Aviv)
java ClientApp BR01 localhost 5000

# Run Branch 2 (Jerusalem)  
java ClientApp BR02 localhost 5000
```

## Communication Flow
```
┌──────────┐    Request     ┌──────────┐
│  Client  │ ─────────────► │  Server  │
│ (BR01)   │                │          │
│          │ ◄───────────── │          │
└──────────┘    Response    └──────────┘
```

## Running Multiple Clients
Open 3 terminals:
1. **Terminal 1**: Run ServerApp
2. **Terminal 2**: Run ClientApp BR01
3. **Terminal 3**: Run ClientApp BR02
