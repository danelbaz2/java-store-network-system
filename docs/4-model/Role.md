# Role

**Package:** `com.storemgmt.model.employee`  
**Type:** Enum

## Purpose

Defines the available employee roles with different permission levels.

## Values

| Role | Description | Permissions |
|------|-------------|-------------|
| `CASHIER` | Handles sales transactions | View inventory, Sell, View employees, Chat |
| `SELLER` | Sales floor employee | Same as CASHIER |
| `MANAGER` | Branch manager | + Monitor chats, Update inventory |
| `ADMIN` | System administrator | + Add/Remove employees, View reports |

## Menu Access by Role

### Regular Menu (CASHIER, SELLER)
1. View Inventory
2. Sell
3. View Employees
4. Start Chat
5. Logout
6. View Customers

### Manager Menu (MANAGER)
All regular options plus:
7. Monitor Chats
8. Update Inventory

### Admin Menu (ADMIN)
All regular options plus:
7. Add Employee
8. Remove Employee
9. Update Inventory
10. View Sell Report

## Usage

```java
Role role = Role.MANAGER;

// Parse from string
Role role = Role.valueOf("MANAGER");

// Check role
if (employee.getRole() == Role.ADMIN) {
    // Allow admin operations
}
```
