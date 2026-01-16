# Java Store Network System - Documentation

## Overview

This is a multi-branch retail store management system built with Java. It uses a client-server architecture where multiple branch clients can connect to a central server to manage inventory, process sales, and communicate with each other.

## Architecture Diagram

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

## Package Structure

| Package | Description |
|---------|-------------|
| `com.storemgmt.client` | Client-side application for branch employees |
| `com.storemgmt.server` | Server-side components handling multiple clients |
| `com.storemgmt.model.store` | Store chain and branch models |
| `com.storemgmt.model.employee` | Employee-related classes |
| `com.storemgmt.model.customer` | Customer types with discount strategies |
| `com.storemgmt.model.product` | Product hierarchy |
| `com.storemgmt.security` | Security utilities (password validation) |
| `com.storemgmt.utils` | Utility classes (JSON loading, logging) |

## Key Features

1. **Multi-Branch Support** - Multiple store branches can operate simultaneously
2. **Employee Management** - Add/remove employees with role-based access
3. **Inventory Management** - Track and update product stock
4. **Sales Processing** - Concurrent customer service with thread pool
5. **Inter-Branch Chat** - Real-time messaging between employees
6. **Sales Reporting** - Generate reports by branch, category, or product
7. **Customer Types** - Different discount rates for New, Returning, and VIP customers

## Documentation Index

### Client Package
- [ClientApp.md](./ClientApp.md) - Client application entry point
- [BranchClient.md](./BranchClient.md) - Network communication handler
- [BranchMenu.md](./BranchMenu.md) - User interface and menu system

### Server Package
- [ServerApp.md](./ServerApp.md) - Server application entry point
- [StoreServer.md](./StoreServer.md) - Main server socket handler
- [ClientHandler.md](./ClientHandler.md) - Individual client request processor
- [CustomerHandler.md](./CustomerHandler.md) - Async customer checkout processor
- [ChatAvailabilityObserver.md](./ChatAvailabilityObserver.md) - Chat notification system

### Model Package
- [Chain.md](./Chain.md) - Store chain container
- [StoreBranch.md](./StoreBranch.md) - Individual branch management
- [Employee.md](./Employee.md) - Employee data model
- [Role.md](./Role.md) - Employee role enumeration
- [Customer.md](./Customer.md) - Abstract customer base class
- [Product.md](./Product.md) - Abstract product base class

### Utilities
- [JsonLoader.md](./JsonLoader.md) - Configuration file handler
- [Logger.md](./Logger.md) - Logging utility
- [PasswordValidator.md](./PasswordValidator.md) - Password validation
