# 🚀 Quick Reference Card - Code Flow

> **One-page summary for quick review before the exam**

---

## 📊 Architecture at a Glance

```
                         ┌─────────────────────────────────────────────────┐
                         │                    SERVER                        │
                         │                                                  │
   ┌──────────┐          │  ┌──────────────┐    ┌────────────────────────┐ │
   │ ClientApp│──Socket──│─▶│ StoreServer  │───▶│    ClientHandler(s)    │ │
   │          │          │  │ (port 8080)  │    │ (one thread per client)│ │
   └──────────┘          │  └──────────────┘    └───────────┬────────────┘ │
        │                │                                   │              │
        │                │                                   ▼              │
   ┌──────────┐          │                          ┌───────────────┐      │
   │BranchMenu│          │   ┌─────────────┐        │CustomerHandler│      │
   │ (UI)     │          │   │    Chain    │        │(thread pool)  │      │
   └──────────┘          │   └──────┬──────┘        └───────────────┘      │
        │                │          │                        │              │
        │                │          ▼                        ▼              │
   ┌──────────┐          │   ┌─────────────┐        ┌───────────────┐      │
   │BranchCli-│          │   │ StoreBranch │◀───────│ sell()        │      │
   │ent       │          │   │ (per branch)│        │ (synchronized)│      │
   └──────────┘          │   └──────┬──────┘        └───────────────┘      │
                         │          │                                       │
                         │    ┌─────┼─────────┐                             │
                         │    ▼     ▼         ▼                             │
                         │ ┌─────┐┌─────┐┌──────────┐                       │
                         │ │Emp. ││Prod.││ Queue    │                       │
                         │ │List ││Map  ││<Customer>│                       │
                         │ └─────┘└─────┘└──────────┘                       │
                         └─────────────────────────────────────────────────┘
```

---

## 🔄 Key Flows

### 1️⃣ Server Startup

```
main() → JsonLoader.load() → generateChain() → StoreServer.start()
              │                    │                    │
         Load config.json    Create branches     Accept connections
```

### 2️⃣ Client Connection

```
Client                              Server
  │                                   │
  ├──── TCP Connect ────────────────▶│ serverSocket.accept()
  │                                   │
  │                                   ├─▶ new ClientHandler()
  │                                   │
  │                                   ├─▶ new Thread(handler).start()
  │◀─────────────────────────────────│
  │        Connection Ready           │
```

### 3️⃣ Request-Response (e.g., LOGIN)

```
BranchMenu                  BranchClient               ClientHandler
    │                           │                           │
    ├─ login(id,pw) ───────────▶│                           │
    │                           ├─ "LOGIN|BR01|empId:E001|  │
    │                           │    password:xxx" ─────────▶│
    │                           │                           ├─▶ handleLogin()
    │                           │                           │    ├─ validate
    │                           │                           │    └─ set currentEmployee
    │                           │◀── "OK|Login successful   │
    │                           │     |employeeName:John" ──│
    │◀── parse and display ─────│                           │
```

### 4️⃣ Sell Flow (The Most Complex!)

```
┌─────────────────────────────────────────────────────────────────────────┐
│ PHASE 1: Request Handling (ClientHandler thread)                        │
├─────────────────────────────────────────────────────────────────────────┤
│ handleSell()                                                            │
│   ├─ Check logged in                                                    │
│   ├─ Check not already serving                                          │
│   ├─ synchronized(branch): customer = branch.getNextCustomer()          │
│   ├─ branch.setCashierBusy(cashierId, true)                            │
│   ├─ new CustomerHandler(customer, branch, cashierId, this)             │
│   └─ branch.submitCustomerForCheckout(handler)  ──────┐                │
│                                                        │                │
│   Return immediately: "OK|Started serving: VIPCustomer John"            │
├────────────────────────────────────────────────────────┼────────────────┤
│ PHASE 2: Async Processing (Thread Pool)               ▼                │
├─────────────────────────────────────────────────────────────────────────┤
│ CustomerHandler.run() [different thread!]                               │
│   ├─ Thread.sleep(2000-5000ms)  // Simulate checkout                   │
│   ├─ branch.sell(customer, cashierId)  // synchronized!                │
│   │       ├─ Loop shopping list                                         │
│   │       ├─ Check stock, update inventory                             │
│   │       ├─ Calculate total                                            │
│   │       └─ Apply discount: sum * customer.getDiscount()               │
│   └─ finally:                                                           │
│         ├─ branch.setCashierBusy(cashierId, false)                     │
│         └─ clientHandler.notifyWaitingEmployees()                       │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 🔐 Synchronization Map

| What                       | How                          | Why                                        |
| -------------------------- | ---------------------------- | ------------------------------------------ |
| `sell()` method            | `synchronized` on method     | Prevents concurrent inventory modification |
| `getNextCustomer()`        | `synchronized(branch)` block | Queue is not thread-safe                   |
| Reports                    | `synchronized(chain)`        | Iterating all branches                     |
| `ChatAvailabilityObserver` | `synchronized static`        | Shared across all handlers                 |
| Add/Remove Employee        | `synchronized(chain)`        | Modifying shared collections               |

---

## 🧬 Polymorphism Example

```java
// StoreBranch.sell() - line 279
double finalPrice = sum * customer.getDiscount();

// At runtime, based on actual customer type:
┌────────────────────┬─────────────────────────┬────────────────┐
│ customer type      │ getDiscount() returns   │ Result         │
├────────────────────┼─────────────────────────┼────────────────┤
│ VIPCustomer        │ 0.5 (from config.json)  │ 50% discount   │
│ ReturningCustomer  │ 0.8 (from config.json)  │ 20% discount   │
│ NewCustomer        │ 1.0 (from config.json)  │ No discount    │
└────────────────────┴─────────────────────────┴────────────────┘
```

---

## 📡 Protocol Format

```
REQUEST:  ACTION|branchId|key1:value1|key2:value2|...
RESPONSE: STATUS|message|key1:value1|...

Examples:
─────────
LOGIN|BR01|employeeId:E001|password:secret123
OK|Login successful|employeeName:John Smith|role:CASHIER

SELL|BR01|
OK|Started serving: (VIPCustomer) David|Customers remaining in queue: 9

GET_INVENTORY|BR01|
OK|Inventory for Main Branch|P001 - NIKE Jean (50 units) - $150.0\n...
```

---

## 🧵 Thread Model Summary

| Component         | Thread Type    | Count            |
| ----------------- | -------------- | ---------------- |
| `StoreServer`     | Main thread    | 1                |
| `ClientHandler`   | One per client | N (unbounded)    |
| `CustomerHandler` | Thread pool    | MAX_CASHIERS (3) |

**Key insight:** `ClientHandler` handles requests but delegates actual sales to the thread pool so it can immediately respond.

---

## ⚡ One-Liner Answers

| If asked...                       | Say...                                                                      |
| --------------------------------- | --------------------------------------------------------------------------- |
| "Why threads per client?"         | "Allows concurrent request handling and real-time push notifications"       |
| "Why thread pool for sales?"      | "Limits concurrent checkouts to MAX_CASHIERS, prevents resource exhaustion" |
| "Why synchronized on sell?"       | "Prevents race condition when two cashiers modify same inventory"           |
| "Why abstract Customer?"          | "Forces subclasses to implement getDiscount() while sharing common code"    |
| "Why sockets not HTTP?"           | "Need real-time bidirectional communication for chat notifications"         |
| "Why config.json for discounts?"  | "Configuration over code - change discounts without recompiling"            |
| "What happens if queue is empty?" | "getNextCustomer() returns null, handleSell() returns error message"        |
| "How is session maintained?"      | "currentEmployee field in ClientHandler - set on login, cleared on logout"  |

---

_Print this page and review before the exam!_
