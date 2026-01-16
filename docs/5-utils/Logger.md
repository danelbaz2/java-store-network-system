# Logger

**Package:** `com.storemgmt.utils`  
**Type:** Interface (Static Utility)

## Purpose

Provides simple file-based logging functionality. Writes timestamped messages to `server_log.txt`.

## Functions

### `write(String message)`
- **Purpose:** Appends a log message to the log file
- **Parameters:** `message` - The message to log
- **Behavior:**
  1. Opens `server_log.txt` in append mode
  2. Formats current timestamp
  3. Writes `{timestamp} {message}`
  4. Auto-closes file

## Log Format

```
2026-01-16 15:30:45 Employee added: E005
2026-01-16 15:31:02 Chat started: E001 -> E003
2026-01-16 15:32:15 Cashier E002 completed sale for: John | Total: $125.50
```

## Usage

```java
Logger.write("Employee added: " + empId);
Logger.write("Chat: " + fromId + " -> " + toId + ": " + message);
Logger.write("Cashier " + cashierId + " completed sale...");
```

## Log File Location

The log file is written to the working directory:
- `{project_dir}/server_log.txt`

## Note

This is implemented as an interface with a static method (Java 8+ feature) rather than a class, allowing it to be used without instantiation.
