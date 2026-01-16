# ClientApp

**Package:** `com.storemgmt.client`  
**Type:** Main Class (Entry Point)

## Purpose

Entry point for the client application. Initializes the connection to the server and launches the branch menu interface.

## Relationships

```
ClientApp
    │
    ├──▶ JsonLoader (loads config.json)
    ├──▶ BranchClient (creates network connection)
    └──▶ BranchMenu (starts user interface)
```

## Functions

### `main(String[] args)`
- **Purpose:** Application entry point
- **Flow:**
  1. Loads configuration from `config.json`
  2. Prompts user for branch ID
  3. Validates branch ID against config
  4. Creates `BranchClient` with server connection details
  5. If connected, creates and runs `BranchMenu`
  6. Disconnects on exit

### `isBranchIdValid(String branchId)`
- **Purpose:** Validates that the entered branch ID exists in the configuration
- **Parameters:** `branchId` - The branch ID to validate
- **Returns:** `true` if valid, `false` otherwise
- **Validation:** Checks against `JsonLoader.getKeys("branches")`

## Usage

```bash
# Run the client application
java com.storemgmt.client.ClientApp

# Prompts for:
# Enter your branchId: B001
```

## Dependencies

- `JsonLoader` - For reading configuration
- `BranchClient` - For network communication
- `BranchMenu` - For user interaction
