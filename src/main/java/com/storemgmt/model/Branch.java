package com.storemgmt.model;

/**
 * Represents a store branch.
 * Minimal model for S1.
 */
public class Branch {

    private final String branchId;
    private final String name;

    public Branch(String branchId, String name) {
        this.branchId = branchId;
        this.name = name;
    }

    public String getBranchId() {
        return branchId;
    }

    public String getName() {
        return name;
    }
}
