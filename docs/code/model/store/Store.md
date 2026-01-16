# Store Classes

**Package:** `com.storemgmt.model.store`

## Overview

The store structure represents a **chain** of clothing stores with multiple **branches**.

```
Chain (Central Management)
    ├── StoreBranch "BR01" (Tel Aviv)
    │       ├── products (inventory)
    │       ├── employees
    │       └── sellRecord (sales history)
    │
    └── StoreBranch "BR02" (Jerusalem)
            ├── products
            ├── employees
            └── sellRecord
```

---

## Chain.java

### Purpose
- Central hub that manages all branches and customers
- Lives on the **server side** - shared across all client connections

### Fields
| Field | Type | Description |
|-------|------|-------------|
| `name` | String | Chain name (e.g., "StoreChain") |
| `branches` | LinkedList<StoreBranch> | All store branches |
| `customers` | LinkedList<Customer> | All registered customers |

### Key Methods

| Method | Description |
|--------|-------------|
| `addBranch(branch)` | Adds a new branch to the chain |
| `getBranchById(id)` | **synchronized** - Thread-safe branch lookup |
| `addCustomer(customer)` | Registers a customer to the chain |
| `getCustomer(id)` | Finds customer by ID |

### Thread Safety
```java
public synchronized StoreBranch getBranchById(String branchId)
```
> Uses `synchronized` because multiple clients access Chain simultaneously

---

## StoreBranch.java

### Purpose
- Represents a single physical store location
- Manages its own inventory, employees, and sales

### Fields
| Field | Type | Description |
|-------|------|-------------|
| `branchId` | String | Unique ID (e.g., "BR01") |
| `name` | String | Location name (e.g., "Tel Aviv") |
| `products` | Map<Product, Integer> | Inventory: Product → Quantity |
| `employees` | LinkedList<Employee> | Branch staff |
| `sellRecord` | Map<Product, Integer> | Sales history: Product → Units sold |

### Key Methods

| Method | Description |
|--------|-------------|
| `sell(Customer)` | **synchronized** - Processes a customer's purchase |
| `getProductById(id)` | Finds product in inventory |
| `updateProductQuantity(p, qty)` | Updates stock level |
| `displayProducts()` | Prints inventory to console |

### sell() Method - The Core Logic

```java
public synchronized void sell(Customer customer) {
    1. Get customer's shopping list
    2. For each product in list:
        a. Check if product exists in branch
        b. Check if enough quantity available
        c. Subtract from inventory
        d. Add to sellRecord
        e. Add up the price
    3. Apply customer's discount
    4. Print original and final price
}
```

---

## Initial Inventory

When a branch is created, it starts with:

| Product | Brand | Quantity |
|---------|-------|----------|
| Jean | ADIDAS, BERSHKA, LEVIS, NIKE | 50 each |
| LongShirt | BERSHKA, LEVIS, POLO | 50 each |
| Bra | NIKE, ADIDAS, BERSHKA | 50 each |
| RunningShoe | NIKE, ADIDAS | 50 each |

---

## Thread Safety Note

`StoreBranch.sell()` is **synchronized** to prevent:
- Two clients buying the last item simultaneously
- Inventory going negative
- Race conditions in sales records
