# StoreBranch

**Package:** `com.storemgmt.model.store`  
**Type:** Data Model (Branch Container)

## Purpose

Represents a single store branch. Manages employees, products, customers, and sales for that location. Contains the thread pool for concurrent customer checkout.

## Relationships

```
StoreBranch
    │
    ├──▶ Employee[] (branch staff)
    ├──▶ Product[] (inventory)
    ├──▶ Customer[] (queue)
    └──▶ ExecutorService (cashier thread pool)
```

## Fields

| Field | Type | Description |
|-------|------|-------------|
| `branchId` | String | Unique branch identifier |
| `name` | String | Branch display name |
| `products` | Map<Product, Integer> | Inventory (product → quantity) |
| `employees` | LinkedList<Employee> | Branch staff |
| `sellRecord` | Map<Product, Integer> | Sales history |
| `customerQueue` | Queue<Customer> | Waiting customers |
| `cashierPool` | ExecutorService | Thread pool (3 threads) |
| `cashierBusyStatus` | Map<String, Boolean> | Tracks busy cashiers |
| `cashierSales` | Map<String, Double> | Sales per cashier |

## Initialization

### `StoreBranch(String branchId, String name)`
- Initializes all collections
- Calls `initializeInventory()` to load products
- Calls `generateCustomers(10)` to create test customers

### `initializeInventory()`
- Loads products from `config.json`
- Path: `branches.{branchId}.inventory.{productId}`
- Creates Product objects via `createProduct()`

### `generateCustomers(int count)`
- Creates random customers (New, Returning, VIP)
- Adds them to the customer queue

## Core Operations

### `sell(Customer customer, String cashierId)`
- **Purpose:** Processes a customer's shopping list
- **Synchronized:** Yes (thread-safe)
- **Flow:**
  1. Iterates customer's shopping list
  2. Checks product availability
  3. Updates inventory and sell record
  4. Applies customer discount
  5. Tracks cashier sales
  6. Saves to `config.json`

### Customer Queue
- `getNextCustomer()` - Polls next customer from queue
- `getQueueSize()` - Returns queue length
- `getCustomerQueue()` - Returns full queue

### Cashier Management
- `isCashierBusy(String id)` - Checks if cashier is serving
- `setCashierBusy(String id, boolean)` - Updates busy status
- `submitCustomerForCheckout(Runnable)` - Submits to thread pool

## Reports

### `getReportByBranch(FileWriter)`
- Total items sold and revenue
- Sales breakdown by cashier

### `getReportByCategory(FileWriter)`
- Sales grouped by ProductType (PANT, SHIRT, etc.)

### `getReportByProduct(FileWriter)`
- Sales grouped by specific product class

### `saveReportToJson(Map, FileWriter)`
- Writes report data in JSON format

## Employee Management
- `getEmployees()` / `getEmployeeById(String)`
- `addEmployee(Employee)` - Adds and saves to config
- `removeEmployee(Employee)` - Removes and saves to config

## Shutdown
- `shutdown()` - Gracefully shuts down thread pool
