package com.storemgmt.model;

public class Employee {
    private final String employeeId;
    private final String fullName;
    private final String phone;
    private final String branchId;
    private final Role role;

    // בשלב הזה נשמור סיסמה כטקסט רגיל כדי להתקדם.
    // בהמשך (S2) נשדרג ל-hash.
    private String password;

    public Employee(String employeeId, String fullName, String phone, String branchId, Role role, String password) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.phone = phone;
        this.branchId = branchId;
        this.role = role;
        this.password = password;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
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

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}
