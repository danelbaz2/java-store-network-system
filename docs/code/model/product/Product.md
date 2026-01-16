# Product Classes

**Package:** `com.storemgmt.model.product`

## Overview

Products use **inheritance** to define different clothing types, each with its own default price.

```
Product (abstract)
    ├── Jean         → $150
    ├── LongShirt    → $100
    ├── Bra          → $80
    └── RunningShoe  → $250
```

---

## Product.java (Abstract Base Class)

### Purpose
- Defines common fields for all products
- Handles equality based on **type + brand** (not ID)

### Fields
| Field | Type | Description |
|-------|------|-------------|
| `productId` | String | Unique ID (e.g., "P001") |
| `productType` | ProductType | Enum: JEAN, SHIRT, BRA, SHOE |
| `brand` | BrandType | Enum: NIKE, ADIDAS, LEVIS, BERSHKA, POLO |
| `price` | double | Price in dollars |

### Key Methods

| Method | Description |
|--------|-------------|
| `getRandomProduct()` | Static - Creates a random product for testing |
| `equals()` | Two products are equal if same **type AND brand** |
| `hashCode()` | Based on type + brand (important for HashMap keys) |

### Equality Logic
```java
// These are considered EQUAL (same type + brand):
new Jean("P001", BrandType.NIKE)
new Jean("P999", BrandType.NIKE)

// These are DIFFERENT (different brand):
new Jean("P001", BrandType.NIKE)
new Jean("P001", BrandType.ADIDAS)
```

> ⚠️ **Why this matters:** In `StoreBranch.products` HashMap, products are keyed by type+brand, not by ID.

---

## ProductType.java (Enum)

```java
JEAN, SHIRT, BRA, SHOE
```

---

## BrandType.java (Enum)

```java
NIKE, ADIDAS, LEVIS, BERSHKA, POLO
```

---

## Subclasses

### Jean.java
- Type: `ProductType.JEAN`
- Default price: `$150`

### LongShirt.java
- Type: `ProductType.SHIRT`
- Default price: `$100`

### Bra.java
- Type: `ProductType.BRA`
- Default price: `$80`

### RunningShoe.java
- Type: `ProductType.SHOE`
- Default price: `$250`

---

## Config Values (config.json)

```json
"products": {
    "Jean": { "price": 150, "initialStock": 50 },
    "LongShirt": { "price": 100, "initialStock": 40 },
    "Bra": { "price": 80, "initialStock": 30 },
    "RunningShoe": { "price": 250, "initialStock": 25 }
}
```
