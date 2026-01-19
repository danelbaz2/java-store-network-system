package com.storemgmt.model.employee;

public class Employee {
    private final String employeeId;
    private final String fullName;
    private String phone;
    private String branchId;
    private Role role;
    private String password;
    private String bankAccount;

    public Employee(String employeeId, String fullName, String phone, String branchId, Role role, String password,
            String bankAccount) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.phone = phone;
        this.branchId = branchId;
        this.role = role;
        this.password = password;
        this.bankAccount = bankAccount;
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

    @Override
    public String toString() {
        return "{" +
                "\"fullName\":\"" + fullName + "\"," +
                "\"phone\":\"" + phone + "\"," +
                "\"branchId\":\"" + branchId + "\"," +
                "\"role\":\"" + role.name() + "\"," +
                "\"password\":\"" + password + "\"," +
                "\"bankAccount\":\"" + bankAccount + "\"" +
                "}";
    }
}
