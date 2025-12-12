package com.storemgmt.store;

import com.storemgmt.model.Branch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BranchStore {

    private final Map<String, Branch> byId = new HashMap<>();

    public void add(Branch branch) {
        byId.put(branch.getBranchId(), branch);
    }

    public boolean exists(String branchId) {
        return byId.containsKey(branchId);
    }

    public Branch findById(String branchId) {
        return byId.get(branchId);
    }

    public Collection<Branch> getAll() {
        return byId.values();
    }
}
