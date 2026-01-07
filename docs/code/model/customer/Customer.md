# Customer Classes

**Package:** `com.storemgmt.model.customer`

## Overview

The customer hierarchy uses **inheritance** to define different customer types with different discount rates.

```
Customer (abstract)
    ├── NewCustomer      → No discount (100%)
    ├── ReturningCustomer → 20% discount (80%)
    └── VIPCustomer      → 50% discount (50%)
```

---

## Customer.java (Abstract Base Class)

### Purpose
- Defines common fields for all customers
- Each customer has a **shopping list** generated randomly

### Fields
| Field | Type | Description |
|-------|------|-------------|
| `fullName` | String | Customer's name |
| `id` | String | Unique identifier |
| `phoneNum` | String | Phone number |
| `shoppingList` | HashMap<Product, Integer> | Products + quantities to buy |

### Key Methods

| Method | Description |
|--------|-------------|
| `getDiscount()` | **Abstract** - Each subclass returns its discount multiplier |
| `startBuying()` | **Abstract** - Placeholder for future purchase behavior |
| `getShoppingList()` | Returns the map of products the customer wants |
| `generateShoppingList()` | Private - Creates random products (1-10 items, 1-60 qty each) |

### Constructor Logic
```
new Customer(name, id, phone)
    └── Calls generateShoppingList()
        └── Creates 1-10 random products with random quantities
```

---

## NewCustomer.java

### Purpose
- Customer with **no discount** (pays full price)

### Discount
- Returns `1.0` (100% of price) from `config.json`

---

## ReturningCustomer.java

### Purpose
- Customer who has shopped before
- Gets **20% discount**

### Discount
- Returns `0.8` from `config.json`

---

## VIPCustomer.java

### Purpose
- Premium customer with highest benefits
- Gets **50% discount**

### Discount
- Returns `0.5` from `config.json`

---

## Config Values (config.json)

```json
"customers": {
    "NewCustomer": 1,
    "ReturningCustomer": 0.8,
    "VIPCustomer": 0.5
}
```

---

## Usage Example

```java
Customer vip = new VIPCustomer("John", "123", "054-1234567");

// Gets 50% discount
double discount = vip.getDiscount();  // returns 0.5

// Get what they want to buy
HashMap<Product, Integer> cart = vip.getShoppingList();
```
