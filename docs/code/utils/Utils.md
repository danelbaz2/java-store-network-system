# Utility Classes

**Package:** `com.storemgmt.utils`

## Overview

Helper classes for configuration loading and logging.

---

## JsonLoader.java

### Purpose
- Loads `config.json` and provides easy access to values
- Uses **Gson** library for JSON parsing

### Usage
```java
// Load once at startup
JsonLoader.load("config.json");

// Get values using dot notation
int port = JsonLoader.getInt("server.port");           // 5000
String host = JsonLoader.getString("server.host");     // "localhost"
double discount = JsonLoader.getInt("customers.VIPCustomer"); // 0.5
```

### Key Methods

| Method | Description |
|--------|-------------|
| `load(path)` | Loads JSON file into memory |
| `getInt(path)` | Gets integer value (e.g., "server.port") |
| `getString(path)` | Gets string value |
| `getKeys(path)` | Gets all keys under a path |

### Config Structure
```json
{
    "server": { "port": 5000, "host": "localhost" },
    "customers": { "NewCustomer": 1, "ReturningCustomer": 0.8, "VIPCustomer": 0.5 },
    "products": { "Jean": { "price": 150 }, ... },
    "branches": { "BR01": "Tel Aviv", "BR02": "Jerusalem" }
}
```

---

## FileLogger.java / ILogger.java

### Purpose
- Logging utility for debugging and audit
- `ILogger` is the interface, `FileLogger` is the implementation

### Usage
```java
ILogger logger = new FileLogger("logs.txt");
logger.log("User logged in");
```

---

## Note on Gson Dependency

The project uses Gson for JSON. It's declared in `pom.xml`:
```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
</dependency>
```
