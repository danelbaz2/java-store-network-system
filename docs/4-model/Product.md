# Product (Abstract)

**Package:** `com.storemgmt.model.product`  
**Type:** Abstract Base Class

## Purpose

Abstract base class for all product types. Defines common product properties and the factory method for creating products from config.

## Relationships

```
Product (abstract)
    │
    ├──▶ Jean (PANT)
    ├──▶ Shorts (PANT)
    ├──▶ LongShirt (SHIRT)
    ├──▶ TShirt (SHIRT)
    ├──▶ Bra (UNDERWEAR)
    ├──▶ Boxer (UNDERWEAR)
    ├──▶ RunningShoe (SHOE)
    └──▶ Sneaker (SHOE)
```

## Fields

| Field | Type | Description |
|-------|------|-------------|
| `productId` | String (final) | Unique ID (e.g., "P001") |
| `productType` | ProductType (final) | Category (PANT, SHIRT, etc.) |
| `brand` | BrandType (final) | Brand (NIKE, ADIDAS, etc.) |
| `price` | double | Price in dollars |

## Functions

### Constructor
```java
Product(String productId, ProductType productType, BrandType brand, double price)
```

### Factory Method
```java
static Product getRandomProduct(String productId)
```
- **Purpose:** Creates a Product from config data
- **Flow:**
  1. Reads brand, price, type from `productCatalog.{productId}`
  2. Switches on type to create correct subclass
  3. Returns the product instance

### Getters/Setters
- `getProductId()` / `getProductType()` / `getBrand()`
- `getPrice()` / `setPrice(double)`

### `equals(Object o)`
- Products are equal if they have the same `productType` and `brand`
- Note: This means two "Nike Jeans" are considered equal regardless of price

### `hashCode()`
- Based on `productType` and `brand`

### `toString()`
- Formats product info for display

## Subclasses

Each subclass simply calls super with the correct ProductType:

```java
public class Jean extends Product {
    public Jean(String productId, BrandType brand, double price) {
        super(productId, ProductType.PANT, brand, price);
    }
}
```

## Related Enums

- **ProductType** - PANT, SHIRT, UNDERWEAR, SHOE
- **BrandType** - LEVIS, NIKE, ADIDAS, BERSHKA, POLO
