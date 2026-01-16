# JsonLoader

**Package:** `com.storemgmt.utils`  
**Type:** Utility Class (Static)

## Purpose

Handles all JSON configuration file operations. Provides static methods to load, read, modify, and save the `config.json` file.

## Fields

| Field | Type | Description |
|-------|------|-------------|
| `json` | JsonObject (static) | In-memory JSON data |

## Functions

### Loading

#### `load(String path)`
- **Purpose:** Loads JSON file into memory
- **Parameters:** `path` - Absolute path to JSON file
- **Called:** Once at application startup

### Reading

#### `getInt(String path)`
- **Purpose:** Gets integer value from nested path
- **Parameters:** `path` - Dot-separated path (e.g., "server.port")
- **Returns:** `int` value

#### `getDouble(String path)`
- **Purpose:** Gets double value from nested path
- **Returns:** `double` value

#### `getString(String path)`
- **Purpose:** Gets string value from nested path
- **Returns:** `String` value

#### `getKeys(String path)`
- **Purpose:** Gets all keys under a path
- **Returns:** `Set<String>` of key names
- **Example:** `getKeys("branches")` → `{"B001", "B002"}`

### Modifying

#### `set(String path, int value)`
- **Purpose:** Updates an integer value
- **Note:** Removes old value and adds new

#### `add(String path, JsonElement value)`
- **Purpose:** Adds a new element at path
- **Creates:** Intermediate objects if needed

#### `addString(String path, String value)`
- **Purpose:** Adds a string value

#### `addInt(String path, int value)`
- **Purpose:** Adds an integer value

#### `remove(String path)`
- **Purpose:** Removes element at path

### Saving

#### `save()`
- **Purpose:** Writes current JSON to `config.json`
- **Format:** Pretty-printed JSON

## Path Format

Paths use dot notation to navigate:
```
"server.host"           → json.server.host
"branches.B001.name"    → json.branches.B001.name
"employees.E001.role"   → json.employees.E001.role
```

## Thread Safety

⚠️ **Warning:** This class is NOT thread-safe!
- Multiple threads calling `set()` and `save()` can cause race conditions
- Consider adding synchronization for concurrent access

## Usage Example

```java
// Load config
JsonLoader.load("config.json");

// Read values
int port = JsonLoader.getInt("server.port");
String name = JsonLoader.getString("employees.E001.fullName");

// Modify values
JsonLoader.set("branches.B001.inventory.P001.stock", 50);
JsonLoader.save();
```
