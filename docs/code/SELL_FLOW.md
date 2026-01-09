# 🛒 Sell Flow - Design Options

This document outlines **all possible approaches** for implementing the sell/purchase logic. Review with your team and choose the best fit.

---

## 📊 Current State Analysis

### What Already Works ✅
| Component | Status | Location |
|-----------|--------|----------|
| `Customer` hierarchy | ✅ Complete | `model/customer/` |
| `getDiscount()` polymorphism | ✅ Working | Each customer subclass |
| `generateShoppingList()` | ✅ Working | `Customer.java` constructor |
| `StoreBranch.sell(Customer)` | ✅ Complete | `StoreBranch.java:91-128` |
| Discount values in config | ✅ Working | `config.json` |

### What Needs Work ⚠️
| Component | Issue |
|-----------|-------|
| `ClientHandler.handleSellProduct()` | Stub - doesn't call `StoreBranch.sell()` |
| `BranchMenu` menus | Option #2 "Sell" is commented out |
| Customer source | Need to decide where customers come from |

---

# 🏗️ Approach Options

## Option A: Manual Customer ID Entry

### How It Works
- Customers are pre-registered in `Chain.customers`
- Employee enters a Customer ID when selling
- Server looks up the customer and processes their shopping list

### Flow
```
Employee: "Sell to customer C001"
    ↓
Server: Find Customer("C001") in Chain.customers
    ↓
Server: Call StoreBranch.sell(customer)
    ↓
Server: Return result with discount applied
```

### Protocol
```
Client → Server: SELL|BR01|sessionId:xxx|customerId:C001
Server → Client: OK|Sale complete|total:275.00|discount:50%
```

### Pros
- Simple to implement
- Tracks specific customers over time
- Can view customer history by ID

### Cons
- Requires managing customer registration separately
- Employee must know/enter customer IDs
- Less realistic store simulation

---

## Option B: Per-Branch Customer Queue (Recommended) ⭐

### How It Works
- Each `StoreBranch` has its own `Queue<Customer>`
- On server start, generate random customers for each branch
- When employee clicks "Sell", the next customer is **dequeued**
- Customer type (VIP, Returning, New) is randomly assigned

### Flow
```
Server Startup:
    BR01.customerQueue = [Customer, Customer, Customer, ...]  (e.g., 15 customers)
    BR02.customerQueue = [Customer, Customer, Customer, ...]  (e.g., 15 customers)

Employee clicks "Sell":
    ↓
Server: Customer next = branch.customerQueue.poll()
    ↓
Server: StoreBranch.sell(next)
    ↓
Server: Return result + queue size remaining
```

### Protocol
```
Client → Server: SELL|BR01|sessionId:xxx
Server → Client: OK|Sale complete|customer:David (VIP)|total:275.00|remaining:14
```

### Console Output Example
```
Choose option: 2

--- NEXT CUSTOMER ---
Name: David Cohen
Type: VIPCustomer
Shopping list: 3 items

Processing sale...
-----------------------------------
✓ Jean NIKE x2 - $300.00
✓ RunningShoe ADIDAS x1 - $250.00
✗ Bra POLO - Not available in branch
-----------------------------------
Subtotal: $550.00
VIP Discount (50%): -$275.00
Final Price: $275.00
-----------------------------------
Sale complete!

Customers waiting: 14
```

### Config Addition
```json
{
    "customers": {
        "queueSizePerBranch": 15,
        "NewCustomer": 1,
        "ReturningCustomer": 0.8,
        "VIPCustomer": 0.5
    }
}
```

### Code Structure
```java
// In StoreBranch.java
private Queue<Customer> customerQueue = new LinkedList<>();

public void generateCustomerQueue(int size) {
    for (int i = 0; i < size; i++) {
        Customer c = createRandomCustomer();
        Logger.write("Customer added: " + c.getName());  // Random type
        customerQueue.add(c);
    }
}

public Customer getNextCustomer() {
    return customerQueue.poll();  // Returns null if empty
}

public int getQueueSize() {
    return customerQueue.size();
}
```

### Pros
- ✅ Demonstrates Queue data structure (good for exams!)
- ✅ Realistic store simulation
- ✅ Simple employee UX - just click "Sell"
- ✅ Leverages existing `generateShoppingList()`
- ✅ Per-branch = more realistic (different customers at each location)

### Cons
- Queue empties over time (may need refill mechanism)
- No specific customer tracking

---

## Option C: Employee Enters Product Details

### How It Works
- Employee manually enters: product type, brand, quantity, customer type
- Server creates a temporary customer or calculates directly
- No pre-registered customers needed

### Flow
```
Employee: "Sell 2x Nike Jeans to a VIP customer"
    ↓
Server: Create temporary VIPCustomer (or calculate directly)
    ↓
Server: Process sale: 2 × $150 × 0.5 = $150
```

### Protocol
```
Client → Server: SELL|BR01|sessionId:xxx|productType:Jean|brand:NIKE|qty:2|customerType:VIP
Server → Client: OK|Sale complete|total:150.00
```

### Pros
- Complete employee control
- No queue management needed
- Works for any product/quantity

### Cons
- More typing for employee
- Doesn't use Customer class hierarchy (less OOP demonstration)
- Less realistic - real stores scan products, don't type

---

## Option D: Hybrid - Queue + Manual Override

### How It Works
- Primary: Per-branch queue (Option B)
- Secondary: Allow manual customer ID for special cases

### Flow
```
Employee: "2" (Sell)
    ↓
System: "Press ENTER for next customer, or type Customer ID"
    ↓
Employee: [ENTER] → Dequeue next customer
    OR
Employee: "C001" → Lookup specific customer
```

### Protocol
```
# Next in queue
Client → Server: SELL|BR01|sessionId:xxx

# Specific customer
Client → Server: SELL|BR01|sessionId:xxx|customerId:C001
```

### Pros
- Flexibility for different scenarios
- Best of both worlds

### Cons
- More complex implementation
- More complex UI

---

## Option E: Shared Chain-Wide Queue

### How It Works
- Single queue in `Chain` (not per-branch)
- Any branch can serve the next customer
- Demonstrates concurrent access

### Flow
```
Server Startup:
    Chain.customerQueue = [Customer, Customer, ...]  (shared)

BR01 sells → takes from shared queue
BR02 sells → takes from same shared queue
```

### Pros
- Shows thread-safe Queue usage
- Simpler setup (one queue vs. multiple)

### Cons
- Less realistic (customers don't "line up" across cities)
- Potential contention between branches

---

# 📋 Comparison Table

| Aspect | Option A (ID) | Option B (Queue/Branch) ⭐ | Option C (Manual) | Option D (Hybrid) |
|--------|---------------|---------------------------|-------------------|-------------------|
| **Complexity** | Low | Medium | Low | High |
| **OOP Demo** | Medium | High | Low | High |
| **Data Structure Demo** | None | Queue | None | Queue |
| **Realism** | Medium | High | Low | High |
| **Employee UX** | Type ID | Click once | Type everything | Flexible |
| **Exam Presentation** | Good | Excellent | Okay | Excellent |

---

# 🔧 Implementation Details (For Chosen Approach)

## If You Choose Option B (Recommended):

### Files to Modify
| File | Change |
|------|--------|
| `StoreBranch.java` | Add `Queue<Customer>`, `generateCustomerQueue()`, `getNextCustomer()` |
| `ServerApp.java` | Call `generateCustomerQueue()` for each branch on startup |
| `ClientHandler.java` | Update `handleSellProduct()` to use `getNextCustomer()` |
| `BranchMenu.java` | Simplify `sell()` to just send request, enable menu option |
| `config.json` | Add `"queueSizePerBranch": 15` |

### Random Customer Generation
```java
private Customer createRandomCustomer() {
    String[] firstNames = {"David", "Sarah", "Moshe", "Yael", "Avi", "Noa"};
    String[] lastNames = {"Cohen", "Levi", "Mizrahi", "Peretz", "Biton"};
    
    String id = "C" + (customerCounter++);
    String name = firstNames[random] + " " + lastNames[random];
    String phone = "05" + randomDigits(8);
    
    int type = random.nextInt(3);
    return switch(type) {
        case 0 -> new NewCustomer(name, id, phone);
        case 1 -> new ReturningCustomer(name, id, phone);
        case 2 -> new VIPCustomer(name, id, phone);
    };
    // Note: shoppingList auto-generated in Customer constructor!
}
```

### Queue Refill Strategy (Optional)
If queue empties during demo:

| Strategy | Description |
|----------|-------------|
| **No refill** | Queue empties = no more customers (realistic) |
| **Auto-refill** | When size < 5, generate 10 more |
| **Admin action** | Admin can trigger "Generate more customers" |

---

# ❓ Open Questions for Your Team

1. **Queue size per branch?** (Suggested: 10-20)

2. **What happens when queue is empty?**
   - Error message "No customers waiting"?
   - Auto-generate more?

3. **Should we show queue size in menu?**
   ```
   2. Sell (15 customers waiting)
   ```

4. **Random shopping list size?** Currently 1-10 products, 1-60 quantity each. Adjust?

5. **Weighted customer types?** Currently equal probability. Should VIPs be rarer?
   ```json
   "customerTypeWeights": {
       "NewCustomer": 50,
       "ReturningCustomer": 35,
       "VIPCustomer": 15
   }
   ```

---

# 📅 Next Steps

1. **Team Discussion** - Review options A-E
2. **Decision** - Pick an approach
3. **Implementation** - Follow the details in this doc
4. **Testing** - Run server with 2 clients, verify concurrent sells

---

*Document created: 2026-01-07*
*Status: PLANNING - No code implementation yet*
