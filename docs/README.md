# Java Store Network System - Documentation

---

## 🎓 Exam Preparation (Start Here!)

> **For the 6-student oral exam - 10 minutes total**
> The professor can ask ANY student about ANY part of the code.

| Document                                                     | Purpose                                                                                                |
| ------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------ |
| 📚 **[STUDY_GUIDE.md](./1-overview/STUDY_GUIDE.md)**         | Complete study guide with reading order, 7 topic areas, potential questions, and critical line numbers |
| 🚀 **[QUICK_REFERENCE.md](./1-overview/QUICK_REFERENCE.md)** | One-page visual summary with diagrams and one-liner answers - print before exam!                       |

---

## 📁 Documentation Structure

```
docs/
├── README.md                          ← You are here (start point)
│
├── 1-overview/                        ← EXAM PREPARATION
│   ├── STUDY_GUIDE.md                    Reading order + questions
│   └── QUICK_REFERENCE.md                One-page cheat sheet
│
├── 2-server/                          ← SERVER SIDE (Threading focus)
│   ├── ServerApp.md                      Entry point
│   ├── StoreServer.md                    Socket server, thread creation
│   ├── ClientHandler.md                  Request handling (CRITICAL)
│   ├── CustomerHandler.md                Async checkout (thread pool)
│   └── ChatAvailabilityObserver.md       Observer pattern
│
├── 3-client/                          ← CLIENT SIDE
│   ├── ClientApp.md                      Entry point
│   ├── BranchClient.md                   Network communication
│   └── BranchMenu.md                     User interface
│
├── 4-model/                           ← DATA LAYER (OOP focus)
│   ├── Chain.md                          Store chain container
│   ├── StoreBranch.md                    Branch logic (CRITICAL)
│   ├── Customer.md                       Abstract customer
│   ├── CustomerSubclasses.md             VIP/Returning/New (polymorphism)
│   ├── Product.md                        Abstract product
│   ├── ProductSubclasses.md              Jean/Bra/Sneaker/etc.
│   ├── Employee.md                       Employee model
│   └── Role.md                           MANAGER/CASHIER enum
│
└── 5-utils/                           ← UTILITIES
    ├── JsonLoader.md                     Configuration management
    ├── Logger.md                         Logging
    └── PasswordValidator.md              Password validation
```

---

## 📖 Recommended Reading Order

### For Complete Understanding (follow the numbers):

| #   | Folder   | File                      | What You Learn                   |
| --- | -------- | ------------------------- | -------------------------------- |
| 1   | —        | **This README**           | Overview, architecture diagram   |
| 2   | 2-server | ServerApp.md              | Server entry point               |
| 3   | 2-server | StoreServer.md            | Socket server, thread-per-client |
| 4   | 2-server | **ClientHandler.md** ⭐   | Request handling, protocol       |
| 5   | 2-server | **CustomerHandler.md** ⭐ | Thread pool, async processing    |
| 6   | 4-model  | Chain.md                  | Store chain container            |
| 7   | 4-model  | **StoreBranch.md** ⭐     | Core business logic              |
| 8   | 4-model  | **Customer.md** ⭐        | Abstract class, polymorphism     |
| 9   | 4-model  | CustomerSubclasses.md     | Discount implementation          |
| 10  | 4-model  | **SELL_FLOW.md** ⭐       | The most complex flow            |
| 11  | 3-client | ClientApp.md              | Client entry point               |
| 12  | 3-client | BranchClient.md           | Network communication            |
| 13  | 3-client | BranchMenu.md             | User interface                   |

> ⭐ = Critical files for the exam

---

## 🏗️ Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                           SERVER SIDE                                │
│  ┌─────────────┐    ┌─────────────┐    ┌──────────────────────┐     │
│  │  ServerApp  │───▶│ StoreServer │───▶│   ClientHandler(s)   │     │
│  └─────────────┘    └─────────────┘    └──────────────────────┘     │
│                            │                      │                  │
│                            ▼                      ▼                  │
│                      ┌─────────┐          ┌───────────────┐         │
│                      │  Chain  │          │CustomerHandler│         │
│                      └─────────┘          └───────────────┘         │
│                            │                                         │
│                            ▼                                         │
│                    ┌─────────────┐                                   │
│                    │ StoreBranch │                                   │
│                    └─────────────┘                                   │
│                     ▲    ▲    ▲                                      │
│            ┌────────┘    │    └────────┐                             │
│            ▼             ▼             ▼                             │
│      ┌──────────┐  ┌──────────┐  ┌──────────┐                       │
│      │ Employee │  │ Product  │  │ Customer │                       │
│      └──────────┘  └──────────┘  └──────────┘                       │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                           CLIENT SIDE                                │
│  ┌─────────────┐    ┌─────────────┐    ┌──────────────────────┐     │
│  │  ClientApp  │───▶│BranchClient │◀──▶│     BranchMenu       │     │
│  └─────────────┘    └─────────────┘    └──────────────────────┘     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📦 Package Structure

| Package                        | Description                                      |
| ------------------------------ | ------------------------------------------------ |
| `com.storemgmt.client`         | Client-side application for branch employees     |
| `com.storemgmt.server`         | Server-side components handling multiple clients |
| `com.storemgmt.model.store`    | Store chain and branch models                    |
| `com.storemgmt.model.employee` | Employee-related classes                         |
| `com.storemgmt.model.customer` | Customer types with discount strategies          |
| `com.storemgmt.model.product`  | Product hierarchy                                |
| `com.storemgmt.security`       | Security utilities (password validation)         |
| `com.storemgmt.utils`          | Utility classes (JSON loading, logging)          |

---

## 🔑 Key Features

1. **Multi-Branch Support** - Multiple store branches can operate simultaneously
2. **Employee Management** - Add/remove employees with role-based access
3. **Inventory Management** - Track and update product stock
4. **Sales Processing** - Concurrent customer service with thread pool
5. **Inter-Branch Chat** - Real-time messaging between employees
6. **Sales Reporting** - Generate reports by branch, category, or product
7. **Customer Types** - Different discount rates for New, Returning, and VIP customers

---

## 🧵 Threading Model (Exam Focus!)

| Component         | Thread Type         | Purpose            |
| ----------------- | ------------------- | ------------------ |
| `StoreServer`     | Main thread         | Accept connections |
| `ClientHandler`   | Thread per client   | Handle requests    |
| `CustomerHandler` | Thread pool (3 max) | Process checkouts  |

---

_Last updated: 2026-01-16_
