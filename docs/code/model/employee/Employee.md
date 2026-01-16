# Employee Classes

**Package:** `com.storemgmt.model.employee`

## Overview

Defines employees who work at store branches.

---

## Employee.java

### Purpose
- Represents a store employee
- Contains login credentials and role

### Fields
| Field | Type | Description |
|-------|------|-------------|
| `employeeId` | String | Unique ID (e.g., "E001") |
| `fullName` | String | Employee's name |
| `phone` | String | Phone number |
| `branchId` | String | Which branch they work at |
| `role` | Role | ADMIN or REGULAR |
| `password` | String | Login password |

### Key Methods

| Method | Description |
|--------|-------------|
| `getEmployeeId()` | Returns employee ID |
| `getFullName()` | Returns name |
| `getRole()` | Returns ADMIN or REGULAR |
| `getPassword()` | Returns password (for login check) |

---

## Role.java (Enum)

```java
public enum Role {
    ADMIN,    // Can add/remove employees, update inventory
    REGULAR   // Can view inventory, sell, chat
}
```

### Permission Differences

| Feature | ADMIN | REGULAR |
|---------|-------|---------|
| View Inventory | ✅ | ✅ |
| Sell Products | ✅ | ✅ |
| View Employees | ✅ | ✅ |
| Send Chat | ✅ | ✅ |
| Add Employee | ✅ | ❌ |
| Remove Employee | ✅ | ❌ |
| Update Inventory | ✅ | ❌ |

---

## Employee Storage

Employees are stored per branch in `StoreBranch.employees` LinkedList.

```java
// Add employee to branch
branch.addEmployee(new Employee("E001", "John", "054-123", "BR01", Role.ADMIN, "Pass123!"));

// Find employee
Employee emp = branch.getEmployeeById("E001");
```
