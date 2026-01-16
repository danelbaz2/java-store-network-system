# Customer (Abstract)

**Package:** `com.storemgmt.model.customer`  
**Type:** Abstract Base Class

## Purpose

Abstract base class for all customer types. Defines common customer properties and the discount strategy pattern.

## Relationships

```
Customer (abstract)
    │
    ├──▶ NewCustomer (no discount)
    ├──▶ ReturningCustomer (small discount)
    └──▶ VIPCustomer (large discount)
```

## Fields

| Field | Type | Description |
|-------|------|-------------|
| `fullName` | String | Customer's name |
| `id` | String | Unique ID (e.g., "C1001") |
| `phoneNum` | String | Phone number |
| `shoppingList` | HashMap<Product, Integer> | Items to purchase |

## Abstract Method

### `getDiscount()`
- **Purpose:** Returns the discount multiplier for this customer type
- **Returns:** `double` (e.g., 1.0 = no discount, 0.9 = 10% off)
- **Implemented by:** Each subclass reads from config

## Functions

### Constructor
```java
Customer(String fullName, String id, String phoneNum)
```
- Sets basic properties
- Calls `generateShoppingList()` automatically

### `generateShoppingList()`
- **Purpose:** Creates random shopping items
- **Behavior:**
  1. Gets all product IDs from config
  2. Randomly selects 1 to N products
  3. Assigns random quantities (1-5)

### Getters
- `getFullName()` / `getId()` / `getShoppingList()`

### `toString()`
- Returns formatted customer info

## Discount Strategy

Each subclass implements `getDiscount()`:

| Class | Config Path | Typical Value |
|-------|-------------|---------------|
| NewCustomer | `customers.NewCustomer` | 1.0 (0% off) |
| ReturningCustomer | `customers.ReturningCustomer` | 0.95 (5% off) |
| VIPCustomer | `customers.VIPCustomer` | 0.85 (15% off) |
