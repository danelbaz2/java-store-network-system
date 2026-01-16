# ProductType

**Package:** `com.storemgmt.model.product`  
**Type:** Enum

## Purpose

Categorizes products into high-level types for reporting and organization.

## Values

| Value | Description | Products |
|-------|-------------|----------|
| `PANT` | Pants/Bottoms | Jean, Shorts |
| `SHIRT` | Upper body wear | LongShirt, TShirt |
| `UNDERWEAR` | Undergarments | Bra, Boxer |
| `SHOE` | Footwear | RunningShoe, Sneaker |

## Usage

```java
// Each product class sets its type in constructor
public class Jean extends Product {
    public Jean(...) {
        super(productId, ProductType.PANT, brand, price);
    }
}

// Used in reports
Map<ProductType, Integer> categoryReport = new HashMap<>();
for (Product p : sellRecord.keySet()) {
    ProductType category = p.getProductType();
    // aggregate by category...
}
```
