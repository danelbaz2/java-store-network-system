package com.storemgmt.model.store;

import java.util.LinkedList;

public class Chain {

    private String name;
    private LinkedList<StoreBranch> branches;

    public Chain(String name) {
        this.name = name;
        this.branches = new LinkedList<StoreBranch>();
    }

    public String getName() {
        return name;
    }

    public LinkedList<StoreBranch> getBranches() {
        return branches;
    }

    public synchronized void addBranch(StoreBranch branch) {
        branches.add(branch);
    }

    // synchronized to prevent multiple threads from accessing the same branch
    public synchronized StoreBranch getBranchById(String branchId) {
        for (StoreBranch branch : branches) {
            if (branch.getBranchId().equals(branchId)) {
                return branch;
            }
        }
        return null;
    }
}
