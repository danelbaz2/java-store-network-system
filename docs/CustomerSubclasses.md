# Customer Subclasses

**Package:** `com.storemgmt.model.customer`

## Overview

All customer subclasses extend `Customer` and implement the `getDiscount()` method to return their specific discount rate from configuration.

## Customer Classes

### NewCustomer

First-time customers with no discount.

```java
public class NewCustomer extends Customer {
    public NewCustomer(String fullName, String id, String phoneNum) {
        super(fullName, id, phoneNum);
    }

    @Override
    public double getDiscount() {
        return JsonLoader.getDouble("customers.NewCustomer");
    }
}
```

**Typical discount:** `1.0` (0% off - full price)

---

### ReturningCustomer

Customers who have shopped before. Get a small discount.

```java
public class ReturningCustomer extends Customer {
    public ReturningCustomer(String fullName, String id, String phoneNum) {
        super(fullName, id, phoneNum);
    }

    @Override
    public double getDiscount() {
        return JsonLoader.getDouble("customers.ReturningCustomer");
    }
}
```

**Typical discount:** `0.95` (5% off)

---

### VIPCustomer

Premium customers with the best discount.

```java
public class VIPCustomer extends Customer {
    public VIPCustomer(String fullName, String id, String phoneNum) {
        super(fullName, id, phoneNum);
    }

    @Override
    public double getDiscount() {
        return JsonLoader.getDouble("customers.VIPCustomer");
    }
}
```

**Typical discount:** `0.85` (15% off)

---

## Configuration

Discounts are configured in `config.json`:

```json
{
    "customers": {
        "NewCustomer": 1.0,
        "ReturningCustomer": 0.95,
        "VIPCustomer": 0.85
    }
}
```

## Usage in Sales

```java
// In StoreBranch.sell()
double sum = 0;
// ... calculate sum of items ...

double finalPrice = sum * customer.getDiscount();
// NewCustomer: $100 * 1.0 = $100
// ReturningCustomer: $100 * 0.95 = $95
// VIPCustomer: $100 * 0.85 = $85
```

## Random Generation

Customers are randomly generated in `StoreBranch.generateCustomers()`:

```java
int type = (int)(Math.random() * 3);
if (type == 0) {
    customer = new NewCustomer(name, id, phone);
} else if (type == 1) {
    customer = new ReturningCustomer(name, id, phone);
} else {
    customer = new VIPCustomer(name, id, phone);
}
```
