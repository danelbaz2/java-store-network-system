# Phase 4: Authentication

## What It Does
Secure login/logout through the server so employees can:
- Login with employee ID + password
- Get a session that identifies them
- Access features based on their role (ADMIN, CASHIER, SALES)

## Key Concepts
- **Session**: Once logged in, client gets a `sessionId` to use in future requests
- **Role-based access**: ADMIN can manage employees, others cannot

---

# Phase 5: Inventory & Sales

## What It Does
The core business operations:
- **GET_INVENTORY**: View products and quantities
- **SELL_PRODUCT**: Decrease stock when selling (synchronized to prevent overselling)

## Race Condition Example
If BR01 and BR02 both try to sell 30 Nike jeans (50 in stock):
- Without sync: Both might succeed → -10 jeans (bug!)
- With sync: One succeeds (20 left), other fails

---

# Phase 6: JSON Persistence

## What It Does
Save and load data to JSON files so data survives server restart:
- `employees.json`
- `inventory.json`
- `customers.json`

---

# Phase 7: Chat System

## What It Does
Real-time messaging between branches:
- BR01 sends message → Server broadcasts to all connected clients
- BR02 receives message instantly
