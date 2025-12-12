package com.storemgmt.security;

public class PasswordPolicy {
    private final int minLength;
    private final boolean requireUpper;
    private final boolean requireLower;
    private final boolean requireDigit;
    private final boolean requireSpecial;

    public PasswordPolicy(int minLength, boolean requireUpper, boolean requireLower, boolean requireDigit, boolean requireSpecial) {
        this.minLength = minLength;
        this.requireUpper = requireUpper;
        this.requireLower = requireLower;
        this.requireDigit = requireDigit;
        this.requireSpecial = requireSpecial;
    }

    public int getMinLength() { return minLength; }
    public boolean isRequireUpper() { return requireUpper; }
    public boolean isRequireLower() { return requireLower; }
    public boolean isRequireDigit() { return requireDigit; }
    public boolean isRequireSpecial() { return requireSpecial; }

    public static PasswordPolicy defaultPolicy() {
        return new PasswordPolicy(8, true, true, true, true);
    }
}
