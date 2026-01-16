# BrandType

**Package:** `com.storemgmt.model.product`  
**Type:** Enum

## Purpose

Defines the available product brands in the store system.

## Values

| Value | Description |
|-------|-------------|
| `LEVIS` | Levi's (denim/casual) |
| `NIKE` | Nike (athletic) |
| `ADIDAS` | Adidas (athletic) |
| `BERSHKA` | Bershka (fashion) |
| `POLO` | Polo (premium) |

## Usage

```java
// Creating a product
Product jeans = new Jean("P001", BrandType.LEVIS, 79.99);

// Parsing from config
String brandStr = JsonLoader.getString("productCatalog.P001.brand");
BrandType brand = BrandType.valueOf(brandStr);  // "NIKE" → BrandType.NIKE
```

## Configuration

Products in `config.json` reference brands by name:
```json
"productCatalog": {
    "P001": {
        "type": "Jean",
        "brand": "LEVIS",
        "price": 79.99
    }
}
```
