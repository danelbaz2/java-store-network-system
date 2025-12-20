package com.storemgmt.model.store;

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
