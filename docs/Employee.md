# Employee

**Package:** `com.storemgmt.model.employee`  
**Type:** Data Model

## Purpose

Represents a store employee with authentication credentials and role information.

## Fields

| Field | Type | Description |
|-------|------|-------------|
| `employeeId` | String (final) | Unique ID (e.g., "E001") |
| `fullName` | String (final) | Employee's full name |
| `phone` | String | Phone number |
| `branchId` | String | Assigned branch |
| `role` | Role | Job role (CASHIER, SELLER, MANAGER, ADMIN) |
| `password` | String | Login password |
| `bankAccount` | String | Bank account number |

## Functions

### Constructor
```java
Employee(String employeeId, String fullName, String phone, 
         String branchId, Role role, String password, String bankAccount)
```

### Getters
- `getEmployeeId()` / `getFullName()` / `getPhone()`
- `getBranchId()` / `getRole()` / `getPassword()`

### `toString()`
- Returns JSON-formatted string for config storage:
```json
{
  "fullName": "...",
  "phone": "...",
  "branchId": "...",
  "role": "...",
  "password": "...",
  "bankAccount": "..."
}
```

## Usage

```java
Employee emp = new Employee("E001", "John Doe", "0501234567", 
    "B001", Role.CASHIER, "Password1", "123456789");
```

## Related Classes

- **Role** - Enum defining valid roles
- **StoreBranch** - Contains employees
- **PasswordValidator** - Validates password requirements
