# Product Subclasses

**Package:** `com.storemgmt.model.product`

## Overview

All product subclasses extend `Product` and simply set the appropriate `ProductType` in their constructor. They don't add any additional behavior.

## Product Classes

### Pants (ProductType.PANT)

#### Jean
```java
public class Jean extends Product {
    public Jean(String productId, BrandType brand, double price) {
        super(productId, ProductType.PANT, brand, price);
    }
}
```

#### Shorts
```java
public class Shorts extends Product {
    public Shorts(String productId, BrandType brand, double price) {
        super(productId, ProductType.PANT, brand, price);
    }
}
```

### Shirts (ProductType.SHIRT)

#### LongShirt
```java
public class LongShirt extends Product {
    public LongShirt(String productId, BrandType brand, double price) {
        super(productId, ProductType.SHIRT, brand, price);
    }
}
```

#### TShirt
```java
public class TShirt extends Product {
    public TShirt(String productId, BrandType brand, double price) {
        super(productId, ProductType.SHIRT, brand, price);
    }
}
```

### Underwear (ProductType.UNDERWEAR)

#### Bra
```java
public class Bra extends Product {
    public Bra(String productId, BrandType brand, double price) {
        super(productId, ProductType.UNDERWEAR, brand, price);
    }
}
```

#### Boxer
```java
public class Boxer extends Product {
    public Boxer(String productId, BrandType brand, double price) {
        super(productId, ProductType.UNDERWEAR, brand, price);
    }
}
```

### Shoes (ProductType.SHOE)

#### RunningShoe
```java
public class RunningShoe extends Product {
    public RunningShoe(String productId, BrandType brand, double price) {
        super(productId, ProductType.SHOE, brand, price);
    }
}
```

#### Sneaker
```java
public class Sneaker extends Product {
    public Sneaker(String productId, BrandType brand, double price) {
        super(productId, ProductType.SHOE, brand, price);
    }
}
```

## Factory Pattern

Products are created via factory methods in `Product.getRandomProduct()` and `StoreBranch.createProduct()`:

```java
switch (type) {
    case "Jean": return new Jean(productId, brandType, price);
    case "Shorts": return new Shorts(productId, brandType, price);
    // ... etc
}
```
