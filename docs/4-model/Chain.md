# Chain

**Package:** `com.storemgmt.model.store`  
**Type:** Data Model (Container)

## Purpose

Represents the entire store chain. Acts as the root container for all branches and serves as a shared synchronization point for thread-safe operations.

## Relationships

```
Chain
    │
    ├──▶ StoreBranch[] (contains multiple branches)
    └──▶ Customer[] (chain-wide customer list)
```

## Fields

| Field | Type | Description |
|-------|------|-------------|
| `name` | String | Chain name (e.g., "FashionStore Chain") |
| `branches` | LinkedList<StoreBranch> | All store branches |
| `customers` | LinkedList<Customer> | Chain-wide customer registry |

## Functions

### `Chain(String name)`
- **Purpose:** Constructor
- **Initializes:** Empty branch and customer lists

### `addBranch(StoreBranch branch)`
- **Purpose:** Adds a new branch to the chain

### `getBranchById(String branchId)`
- **Purpose:** Finds a branch by its ID
- **Returns:** `StoreBranch` or `null` if not found
- **Thread Safety:** `synchronized` method

### `getBranches()`
- **Purpose:** Returns all branches
- **Used for:** Report generation, iteration

### Customer Management
- `addCustomer(Customer)` - Adds customer to chain registry
- `getCustomer(String id)` - Retrieves customer by ID
- `getCustomers()` - Returns all customers

## Synchronization

The `Chain` object is commonly used as a **synchronization lock**:

```java
synchronized (chain) {
    // Thread-safe operations on branches/products
}
```

This ensures only one thread modifies chain data at a time.
