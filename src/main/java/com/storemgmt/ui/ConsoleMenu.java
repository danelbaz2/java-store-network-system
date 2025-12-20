package com.storemgmt.ui;

import com.storemgmt.model.Employee;
import com.storemgmt.model.Role;
import com.storemgmt.model.UserSession;
import com.storemgmt.security.PasswordPolicy;
import com.storemgmt.service.AuthService;
import com.storemgmt.service.SessionManager;
import com.storemgmt.store.BranchStore;
import com.storemgmt.store.EmployeeStore;
import com.storemgmt.utils.FileLogger;
import com.storemgmt.utils.ILogger;
import com.storemgmt.model.Branch;
import com.storemgmt.store.BranchStore;


import java.util.Scanner;

/**
 * Simple console menu to simulate the system behavior.
 * This is a temporary UI that will grow during the project.
 */
public class ConsoleMenu {

    private final Scanner scanner = new Scanner(System.in);

    private final ILogger logger = new FileLogger("logs.txt");
    private final EmployeeStore employeeStore = new EmployeeStore("employees.csv");
    private final BranchStore branchStore = new BranchStore();
    private final SessionManager sessionManager = new SessionManager();
    private final AuthService authService = new AuthService(
            employeeStore,
            sessionManager,
            PasswordPolicy.defaultPolicy(),
            logger
    );

    public ConsoleMenu() {
        // Demo branches (temporary, in-memory)
        branchStore.add(new Branch("BR01", "Main Branch"));
        branchStore.add(new Branch("BR02", "North Branch"));
    }


    // Holds current logged-in sessionId (null if not logged in)
    private String currentSessionId = null;

    // Returns the currently logged-in employee, or null if not logged in
    private Employee getCurrentEmployee() {
        if (currentSessionId == null) return null;

        UserSession session = sessionManager.getSession(currentSessionId);
        if (session == null || !session.isActive()) {
            currentSessionId = null;
            return null;
        }

        return employeeStore.findById(session.getEmployeeId());
    }

    // ADMIN-only action: create a new employee in the in-memory store
    private void createEmployeeAdminOnly() {
        Employee current = getCurrentEmployee();
        if (current == null) {
            System.out.println("Not logged in.");
            return;
        }

        // Role-based permission check
        if (current.getRole() != Role.ADMIN) {
            System.out.println("Access denied: ADMIN only.");
            return;
        }

        System.out.print("New employee ID: ");
        String newId = scanner.nextLine().trim();

        if (employeeStore.exists(newId)) {
            System.out.println("Employee ID already exists.");
            return;
        }

        System.out.print("Full name: ");
        String fullName = scanner.nextLine();

        System.out.print("National ID: ");
        String nationalId = scanner.nextLine();

        System.out.print("Bank Account ID: ");
        String bankAccountId = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Branch ID: ");
        String branchId = scanner.nextLine();

        if (!branchStore.exists(branchId)) {
        System.out.println("Invalid Branch ID. Available branches:");
        for (Branch b : branchStore.getAll()) {
            System.out.println("- " + b.getBranchId() + " (" + b.getName() + ")");
        }
        return;
    }

        System.out.print("Role (ADMIN/CASHIER/SELLER/SHIFT_MANAGER): ");
        String roleInput = scanner.nextLine().trim().toUpperCase();

        Role role;
        try {
            role = Role.valueOf(roleInput);
        } catch (Exception ex) {
            System.out.println("Invalid role.");
            return;
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Validate password against current policy
        var validation = com.storemgmt.security.PasswordValidator.validate(
                password,
                com.storemgmt.security.PasswordPolicy.defaultPolicy()
        );

        if (!validation.isValid()) {
            System.out.println("Password is not valid: " + validation.getMessage());
            return;
        }


        // Create employee (temporary: plain text password, in-memory only)
        Employee newEmp = new Employee(newId ,nationalId,fullName,phone,bankAccountId,branchId,role,password);
        employeeStore.add(newEmp);

        logger.info("Admin created employee. adminId=" + current.getEmployeeId() + ", newEmployeeId=" + newId);
        System.out.println("Employee created successfully.");
    }

    // ADMIN-only action: list all employees currently in the in-memory store
    private void listEmployeesAdminOnly() {
        Employee current = getCurrentEmployee();
        if (current == null) {
            System.out.println("Not logged in.");
            return;
        }

        // Role-based permission check
        if (current.getRole() != Role.ADMIN) {
            System.out.println("Access denied: ADMIN only.");
            return;
        }

        System.out.println("=== Employees ===");
        for (Employee e : employeeStore.getAll()) {
            System.out.println(
                    "ID: " + e.getEmployeeId()
                    + " | National ID: " + e.getNationalId()
                    + " | Name: " + e.getFullName()
                    + " | Role: " + e.getRole()
                    + " | Branch: " + e.getBranchId()
            );
        }
    }

    // ADMIN-only action: update an existing employee.
    // Press Enter to keep current value.
    private void updateEmployeeAdminOnly() {
    Employee current = getCurrentEmployee();
    if (current == null) {
        System.out.println("Not logged in.");
        return;
    }
    if (current.getRole() != Role.ADMIN) {
        System.out.println("Access denied: ADMIN only.");
        return;
    }

    System.out.print("Employee ID to update: ");
    String employeeId = scanner.nextLine().trim();

    Employee existing = employeeStore.findById(employeeId);
    if (existing == null) {
        System.out.println("Employee not found.");
        return;
    }

    System.out.print("New national ID: ");
    String nationalId = scanner.nextLine();

    System.out.print("New bank account ID: ");
    String bankAccountId = scanner.nextLine();

    System.out.println("Press Enter to keep current value.");

    System.out.print("Full name (" + existing.getFullName() + "): ");
    String fullName = scanner.nextLine();
    if (fullName.isBlank()) fullName = existing.getFullName();

    System.out.print("Phone (" + existing.getPhone() + "): ");
    String phone = scanner.nextLine();
    if (phone.isBlank()) phone = existing.getPhone();

    System.out.print("Branch ID (" + existing.getBranchId() + "): ");
    String branchId = scanner.nextLine().trim();
    if (branchId.isBlank()) {
        branchId = existing.getBranchId();
    } else if (!branchStore.exists(branchId)) {
        System.out.println("Invalid Branch ID. Available branches:");
        for (Branch b : branchStore.getAll()) {
            System.out.println("- " + b.getBranchId() + " (" + b.getName() + ")");
        }
        return;
    }

    System.out.print("Role (" + existing.getRole() + "): ");
    String roleInput = scanner.nextLine().trim();
    Role role = existing.getRole();
    if (!roleInput.isBlank()) {
        try {
            role = Role.valueOf(roleInput.toUpperCase());
        } catch (Exception ex) {
            System.out.println("Invalid role.");
            return;
        }
    }

    System.out.print("Password (leave empty to keep current): ");
    String password = scanner.nextLine();
    if (password.isBlank()) {
        password = existing.getPassword();
    } else {
        var validation = com.storemgmt.security.PasswordValidator.validate(
                password,
                com.storemgmt.security.PasswordPolicy.defaultPolicy()
        );
        if (!validation.isValid()) {
            System.out.println("Password is not valid: " + validation.getMessage());
            return;
        }
    }

    Employee updated = new Employee(employeeId, nationalId, bankAccountId, fullName, phone, branchId, role, password);
    employeeStore.update(updated);

    logger.info("Admin updated employee. adminId=" + current.getEmployeeId() + ", employeeId=" + employeeId);
    System.out.println("Employee updated successfully.");
}



    // ADMIN-only action: delete an employee from the store.
    private void deleteEmployeeAdminOnly() {
        Employee current = getCurrentEmployee();
        if (current == null) {
            System.out.println("Not logged in.");
            return;
        }
        if (current.getRole() != Role.ADMIN) {
            System.out.println("Access denied: ADMIN only.");
            return;
        }

        System.out.print("Employee ID to delete: ");
        String employeeId = scanner.nextLine().trim();

        if (employeeStore.findById(employeeId) == null) {
            System.out.println("Employee not found.");
            return;
        }

        // Prevent deleting yourself (simple safety rule)
        if (employeeId.equals(current.getEmployeeId())) {
            System.out.println("You cannot delete your own account while logged in.");
            return;
        }

        // Optional: prevent deleting a currently logged-in employee
        if (sessionManager.isEmployeeLoggedIn(employeeId)) {
            System.out.println("Cannot delete: employee is currently logged in.");
            return;
        }

        System.out.print("Are you sure? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("y")) {
            System.out.println("Delete cancelled.");
            return;
        }

        employeeStore.delete(employeeId);

        logger.info("Admin deleted employee. adminId=" + current.getEmployeeId() + ", employeeId=" + employeeId);
        System.out.println("Employee deleted successfully.");
    }

    
    public void run() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> login();
                    case "2" -> whoAmI();
                    case "3" -> logout();
                    case "4" -> {
                        System.out.println("Bye!");
                        return;
                    }
                    case "5" -> createEmployeeAdminOnly();
                    case "6" -> listEmployeesAdminOnly();
                    case "7" -> updateEmployeeAdminOnly();
                    case "8" -> deleteEmployeeAdminOnly();

                    default -> System.out.println("Unknown option");
                }
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }

            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("=== Store Management System ===");
        System.out.println("1) Login");
        System.out.println("2) Who am I");
        System.out.println("3) Logout");
        System.out.println("4) Exit");
        System.out.println("5) Create employee (ADMIN only)");
        System.out.println("6) List employees (ADMIN only)");
        System.out.println("7) Update employee (ADMIN only)");
        System.out.println("8) Delete employee (ADMIN only)");
        System.out.print("Choose: ");
    }

    private void login() {
        if (currentSessionId != null) {
            System.out.println("Already logged in. Logout first.");
            return;
        }

        System.out.print("Employee ID: ");
        String employeeId = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        UserSession session = authService.login(employeeId, password);
        currentSessionId = session.getSessionId();

        System.out.println("Login successful. Session ID: " + currentSessionId);
    }

    private void whoAmI() {
        if (currentSessionId == null) {
            System.out.println("Not logged in.");
            return;
        }

        UserSession session = sessionManager.getSession(currentSessionId);
        if (session == null || !session.isActive()) {
            currentSessionId = null;
            System.out.println("Session expired/not found.");
            return;
        }

        System.out.println("You are logged in:");
        System.out.println("Employee ID: " + session.getEmployeeId());
        System.out.println("Branch ID: " + session.getBranchId());
        System.out.println("Login time: " + session.getLoginTime());
    }

    private void logout() {
        if (currentSessionId == null) {
            System.out.println("Not logged in.");
            return;
        }

        authService.logout(currentSessionId);
        currentSessionId = null;

        System.out.println("Logged out.");
    }
}
