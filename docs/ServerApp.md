# ServerApp

**Package:** `com.storemgmt.server`  
**Type:** Main Class (Entry Point)

## Purpose

Entry point for the server application. Initializes the store chain data model from configuration and starts the server.

## Relationships

```
ServerApp
    │
    ├──▶ JsonLoader (loads config.json)
    ├──▶ Chain (creates data model)
    │       └──▶ StoreBranch (creates branches)
    │               └──▶ Employee (creates employees)
    └──▶ StoreServer (starts TCP server)
```

## Functions

### `main(String[] args)`
- **Purpose:** Application entry point
- **Flow:**
  1. Loads configuration from `config.json`
  2. Generates the Chain data model
  3. Creates and starts `StoreServer`

### `generateChain()`
- **Purpose:** Creates the root Chain object and populates it
- **Returns:** Fully initialized `Chain` with all branches

### `GenerateBranches(Chain chain)`
- **Purpose:** Creates all `StoreBranch` objects from config
- **Behavior:**
  - Reads branch IDs from `branches` config key
  - Creates `StoreBranch` for each
  - Calls `GenerateEmployees()` for each branch

### `GenerateEmployees(StoreBranch branch)`
- **Purpose:** Creates `Employee` objects for a specific branch
- **Behavior:**
  - Reads all employees from `employees` config key
  - Filters by matching `branchId`
  - Creates `Employee` with full details (name, phone, role, password, bank account)

## Configuration Dependencies

Expects `config.json` with structure:
```json
{
  "server": { "host": "...", "port": ... },
  "branches": {
    "B001": { "branchName": "..." }
  },
  "employees": {
    "E001": { "fullName": "...", "branchId": "B001", ... }
  }
}
```

## Usage

```bash
# Run the server
java com.storemgmt.server.ServerApp
```
