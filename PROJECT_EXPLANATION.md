# Store Network System - Project Explanation

## Overview

This is a multi-threaded client-server store management system built in Java. It simulates a retail chain with multiple branches, employees, customers, and products.

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                          SERVER                                  │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │ StoreServer │───▶│ClientHandler│───▶│ StoreBranch │         │
│  │  (main)     │    │  (per user) │    │ (per store) │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│         │                  │                  │                 │
│         │                  │           ┌──────┴──────┐         │
│         │                  │           │CustomerHandler│        │
│         │                  │           │ (per sale)   │        │
│         │                  │           └─────────────┘         │
└─────────┼──────────────────┼────────────────────────────────────┘
          │                  │
          │    TCP Socket    │
          │                  │
┌─────────┼──────────────────┼────────────────────────────────────┐
│         ▼                  ▼              CLIENT                 │
│  ┌─────────────┐    ┌─────────────┐                             │
│  │ ClientApp   │───▶│BranchClient │                             │
│  │  (main)     │    │(connection) │                             │
│  └─────────────┘    └─────────────┘                             │
│         │                  │                                     │
│         ▼                  │                                     │
│  ┌─────────────┐           │                                     │
│  │ BranchMenu  │◀──────────┘                                     │
│  │ (user UI)   │                                                 │
│  └─────────────┘                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Key Concepts

### 1. Synchronization

#### Why do we need `synchronized`?

When multiple threads access shared data simultaneously, we can have **race conditions** — where the outcome depends on the timing of thread execution.

#### Example: `Chain.getBranchById()` and `Chain.addBranch()`

```java
public synchronized StoreBranch getBranchById(String branchId) {
    for (StoreBranch branch : branches) {
        if (branch.getBranchId().equals(branchId)) {
            return branch;
        }
    }
    return null;
}

public synchronized void addBranch(StoreBranch branch) {
    branches.add(branch);
}
```

**Why synchronized?** If one thread is iterating through `branches` while another thread adds a new branch, we can get:

- `ConcurrentModificationException` — iterator detects modification
- Corrupted data — LinkedList internal pointers become inconsistent
- Skipped elements — iterator might miss elements

#### Example: `StoreBranch.sell()`

```java
public synchronized double sell(Customer customer, String cashierId) {
    // ... modifies products, sellRecord, cashierSales
}
```

**Why synchronized on the branch?** The `sell` method modifies shared branch-level resources (inventory, sales records). Without synchronization, two cashiers could read "5 items in stock" simultaneously and both try to sell 4, causing overselling.

---

### 2. Thread Pool (`ExecutorService`)

```java
private static final int MAX_CASHIERS = 3; // simulating 3 cashiers in a branch
private ExecutorService cashierPool = Executors.newFixedThreadPool(MAX_CASHIERS);
```

**Purpose:** Limits how many cashiers can process customers simultaneously. Additional requests wait in a queue.

**Note:** The thread pool limits _concurrency_, but `synchronized` on `sell()` is still needed to protect shared data from race conditions among those 3 threads.

---

### 3. Client-Server Communication

#### The Listener Thread (in `BranchClient.connect()`)

The client has a **listener thread** that continuously reads messages from the server:

```java
listenerThread = new Thread(() -> {
    while (connected) {
        String line = in.readLine();  // Blocks until message arrives

        // Route messages based on prefix
        if (line.startsWith("CHAT_REQUEST|")) {
            menu.onChatRequest(...);
        } else if (line.startsWith("CHAT_MESSAGE|")) {
            menu.onChatMessage(...);
        } else {
            // Regular command response
            synchronized (responseLock) {
                pendingResponse = line;
                responseLock.notify();  // Wake up the waiting send() method
            }
        }
    }
});
```

**Why a separate thread?**

- The main thread handles user input
- The listener thread receives asynchronous messages (like chat requests)
- Without this, the client couldn't receive chat messages while the user is typing

#### The `wait()`/`notify()` Pattern

```java
public String[] send(String message) {
    synchronized (responseLock) {
        pendingResponse = null;
        out.println(message);

        while (pendingResponse == null && connected) {
            responseLock.wait(120000);  // Wait up to 2 minutes
        }

        return parseResponse(pendingResponse);
    }
}
```

**Flow:**

1. Main thread sends a message and calls `wait()` (sleeps)
2. Listener thread receives the response
3. Listener thread sets `pendingResponse` and calls `notify()`
4. Main thread wakes up and returns the response

**Why timeout (120000ms)?** Prevents the client from hanging forever if the server crashes.

---

### 4. `equals()` and `hashCode()` Override

In `Product.java`:

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Product product = (Product) o;
    return productType == product.productType && brand == product.brand;
}

@Override
public int hashCode() {
    return Objects.hash(productType, brand);
}
```

**Why override?**

- Products are used as keys in `HashMap` (inventory, sellRecord)
- Default `equals()` compares memory references (not useful)
- We want "Nike Jean" objects to be equal regardless of productId

**Why not compare by productId?**

- We track inventory by category (type + brand), not individual items
- "How many Nike Jeans in stock?" vs "How many P001s?"

**The contract:** If `equals()` returns true, `hashCode()` must return the same value.

---

### 5. `Vector` vs `ArrayList`

In `Customer.generateShoppingList()`:

```java
ArrayList<String> productIds = new ArrayList<>(JsonLoader.getKeys("productCatalog"));
```

**Why ArrayList instead of Vector?**

- `Vector` is synchronized (thread-safe) but slower
- `productIds` is a local variable — only one thread accesses it
- No synchronization needed → `ArrayList` is more efficient

---

### 6. Chat Availability Observer Pattern

```java
public static synchronized void subscribe(String requesterEmpId, String targetEmpId) {
    List<String> requesters = pendingChatRequests.get(targetEmpId);
    if (requesters == null) {
        requesters = new ArrayList<>();
        pendingChatRequests.put(targetEmpId, requesters);
    }
    requesters.add(requesterEmpId);
}
```

**Purpose:** Tracks who wants to chat with whom. When an employee becomes available, all waiting requesters are notified.

---

## File Structure

```
src/main/java/com/storemgmt/
├── client/
│   ├── ClientApp.java       # Client entry point
│   ├── BranchClient.java    # Socket connection + listener thread
│   └── BranchMenu.java      # User interface (CLI menu)
├── server/
│   ├── ServerApp.java       # Server entry point
│   ├── StoreServer.java     # Accepts client connections
│   ├── ClientHandler.java   # Handles one client (runs in own thread)
│   ├── CustomerHandler.java # Processes one sale (runs in cashier pool)
│   └── ChatAvailabilityObserver.java  # Manages chat waiting list
├── model/
│   ├── store/
│   │   ├── Chain.java       # The retail chain (has multiple branches)
│   │   └── StoreBranch.java # One store (inventory, employees, cashier pool)
│   ├── customer/
│   │   ├── Customer.java    # Abstract base class
│   │   ├── NewCustomer.java
│   │   ├── ReturningCustomer.java
│   │   └── VIPCustomer.java
│   ├── employee/
│   │   └── Employee.java
│   └── product/
│       ├── Product.java     # Abstract base class
│       ├── ProductType.java # Enum (PANTS, SHIRTS, etc.)
│       ├── BrandType.java   # Enum (NIKE, ADIDAS, etc.)
│       └── [specific products: Jean, TShirt, Sneaker, etc.]
└── utils/
    ├── JsonLoader.java      # Loads/saves config.json
    └── Logger.java          # Logging utility
```

---

## Running the Project

### Start the Server:

```bash
run_server.bat
```

### Start a Client:

```bash
run_client.bat
```

---

## Key Exam Topics

1. **Why synchronized?** → Prevent race conditions on shared data
2. **Thread pool purpose?** → Limit concurrent operations (cashiers)
3. **Why listener thread?** → Handle async messages while user types
4. **wait()/notify()** → Thread coordination without busy-waiting
5. **equals()/hashCode()** → Required for HashMap keys, must compare same fields
6. **Vector vs ArrayList** → Vector is synchronized (overkill for local variables)
