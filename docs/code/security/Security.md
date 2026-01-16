# Security Classes

**Package:** `com.storemgmt.security`

## Overview

Handles password validation for employee accounts.

---

## PasswordValidator.java

### Purpose
- Validates passwords meet minimum security requirements
- Used when adding new employees

### Rules

| Rule | Requirement |
|------|-------------|
| Length | At least 8 characters |
| Uppercase | At least 1 uppercase letter (A-Z) |
| Lowercase | At least 1 lowercase letter (a-z) |
| Digit | At least 1 number (0-9) |

### Usage
```java
boolean valid = PasswordValidator.isValid("Pass123!");  // true
boolean weak = PasswordValidator.isValid("password");   // false (no uppercase/digit)
```

### Method

```java
public static boolean isValid(String password) {
    if (password.length() < 8) return false;
    if (!hasUppercase(password)) return false;
    if (!hasLowercase(password)) return false;
    if (!hasDigit(password)) return false;
    return true;
}
```

---

## Where It's Used

- `ClientHandler.handleAddEmployee()` - Validates password before creating employee
