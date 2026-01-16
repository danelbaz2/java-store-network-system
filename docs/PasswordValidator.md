# PasswordValidator

**Package:** `com.storemgmt.security`  
**Type:** Utility Class

## Purpose

Validates passwords against security requirements. Used when adding new employees.

## Requirements

Passwords must meet all of the following:
1. Not null or empty
2. At least 8 characters long
3. Contains at least one uppercase letter
4. Contains at least one lowercase letter
5. Contains at least one digit

## Functions

### `validate(String password)`
- **Purpose:** Validates password and returns error message or "valid"
- **Parameters:** `password` - The password to validate
- **Returns:** 
  - `"valid"` if password meets all requirements
  - Error message string if validation fails

### `isValid(String password)`
- **Purpose:** Quick boolean check
- **Returns:** `true` if valid, `false` otherwise

### Private Helpers
- `hasUppercase(String)` - Checks for uppercase letter
- `hasLowercase(String)` - Checks for lowercase letter
- `hasDigit(String)` - Checks for digit

## Validation Messages

| Issue | Message |
|-------|---------|
| Null/empty | "Password cannot be empty" |
| Too short | "Password must be at least 8 characters" |
| No uppercase | "Password must contain at least one uppercase letter" |
| No lowercase | "Password must contain at least one lowercase letter" |
| No digit | "Password must contain at least one digit" |

## Usage

```java
// In BranchMenu.readPassword()
String validationResult = PasswordValidator.validate(input);
if (validationResult.equals("valid")) {
    return input;  // Password is acceptable
}
System.out.println("  " + validationResult + ". Please try again.");
```

## Valid Password Examples

✅ `Password1` - 9 chars, has upper, lower, digit  
✅ `SecurePass123` - All requirements met  
❌ `password1` - No uppercase  
❌ `PASSWORD1` - No lowercase  
❌ `Password` - No digit  
❌ `Pass1` - Too short
