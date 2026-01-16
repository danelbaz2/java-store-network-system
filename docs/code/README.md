# Code Documentation

This folder contains documentation for each Java package/class.

## Structure

```
docs/code/
├── model/
│   ├── customer/       → Customer hierarchy (Customer, NewCustomer, VIP, Returning)
│   ├── product/        → Product hierarchy (Product, Jean, Bra, etc.)
│   ├── store/          → Chain and StoreBranch
│   ├── employee/       → Employee and Role
│   └── session/        → UserSession
├── server/             → StoreServer, ClientHandler, ServerApp
├── client/             → BranchClient, BranchMenu, ClientApp
├── service/            → AuthService, SessionManager
├── security/           → PasswordValidator
└── utils/              → JsonLoader, FileLogger
```

## How to Use

Each `.md` file explains:
- **Purpose** - What the class does
- **Key Methods** - Important methods and their logic
- **Flow** - How it connects to other classes
