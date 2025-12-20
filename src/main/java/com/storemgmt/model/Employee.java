package com.storemgmt.model;

/**
 * Represents an employee in the store management system.
 * Contains personal details, role, branch assignment and authentication data.
 */
public class Employee {

    // Unique internal employee number
    private final String employeeId;

    // Government-issued national ID number (e.g. Teudat Zehut)
    private final String nationalId;

    // Employee full name
    private final String fullName;

    // Employee phone number
    private final String phone;

    // Bank account identifier for salary payments
    private final String bankAccountId;

    // Branch identifier the employee is assigned to
    private final String branchId;

    // Employee role (ADMIN, CASHIER, SELLER, SHIFT_MANAGER)
    private final Role role;

    // Password used for authentication (stored hashed in real systems)
    private String password;

    /**
     * Constructs a new Employee object.
     */
    public Employee(
            String employeeId,
            String nationalId,
            String fullName,
            String phone,
            String bankAccountId,
            String branchId,
            Role role,
            String password
    ) {
        this.employeeId = employeeId;
        this.nationalId = nationalId;
        this.fullName = fullName;
        this.phone = phone;
        this.bankAccountId = bankAccountId;
        this.branchId = branchId;
        this.role = role;
        this.password = password;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getNationalId() {
        return nationalId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getBankAccountId() {
        return bankAccountId;
    }

    public String getBranchId() {
        return branchId;
    }

    public Role getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Updates the employee password.
     */
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}
