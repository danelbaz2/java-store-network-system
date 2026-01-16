# CustomerHandler

**Package:** `com.storemgmt.server`  
**Type:** Async Task (Runnable)

## Purpose

Handles the asynchronous processing of a customer sale. Runs in the branch's thread pool to simulate checkout time and process the sale.

## Relationships

```
CustomerHandler
    │
    ├──▶ Customer (customer being served)
    ├──▶ StoreBranch (branch processing sale)
    ├──▶ ClientHandler (to notify waiting employees)
    └──▶ Logger (logs sale completion)
```

## Fields

| Field | Type | Description |
|-------|------|-------------|
| `customer` | Customer | The customer being served |
| `branch` | StoreBranch | The branch processing the sale |
| `cashierId` | String | ID of the cashier handling the sale |
| `clientHandler` | ClientHandler | Reference for post-sale notifications |

## Functions

### `CustomerHandler(Customer, StoreBranch, String, ClientHandler)`
- **Purpose:** Constructor
- **Parameters:**
  - `customer` - Customer to process
  - `branch` - Branch where sale occurs
  - `cashierId` - Employee ID of cashier
  - `clientHandler` - For notifying waiting employees

### `run()`
- **Purpose:** Processes the customer sale asynchronously
- **Flow:**
  1. Simulates checkout time (2-5 seconds random delay)
  2. Calls `branch.sell(customer, cashierId)`
  3. Logs the completed sale
  4. **Finally block:**
     - Sets cashier as not busy
     - Calls `clientHandler.notifyWaitingEmployees()`

## Thread Pool Execution

CustomerHandler is executed via:
```java
branch.submitCustomerForCheckout(handler);
// Uses: cashierPool.execute(customerHandler)
```

The branch has a fixed thread pool of `MAX_CASHIERS = 3` threads.

## Sale Simulation

The random delay (2000-5000ms) simulates realistic checkout time:
```java
Thread.sleep(2000 + (int)(Math.random() * 3000));
```

## Post-Sale Notification

After the sale completes, `notifyWaitingEmployees()` is called to:
- Check if any employees were waiting to chat with this cashier
- Send availability notifications to those employees
