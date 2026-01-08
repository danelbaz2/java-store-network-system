package com.storemgmt.server;

import com.storemgmt.model.employee.Employee;
import com.storemgmt.model.employee.Role;
import com.storemgmt.model.store.Chain;
import com.storemgmt.model.store.StoreBranch;
import com.storemgmt.utils.JsonLoader;

import java.nio.file.Paths;
import java.util.Set;

/**
 * Entry point for the server application.
 * 
 * This class:
 * 1. Creates the Chain with sample data
 * 2. Sets up 2 branches (Tel Aviv, Jerusalem)
 * 3. Adds sample employees to each branch
 * 4. Starts the StoreServer on port 5000
 */
public class ServerApp {
    public static void main(String[] args) {
        JsonLoader.load(Paths.get(System.getProperty("user.dir"), "config.json").toString());
        int port = JsonLoader.getInt("server.port");

        // Create the chain (central data store)
        Chain chain = generateChain();

        // Create and start the server
        StoreServer server = new StoreServer(chain, port);

        // Start the server (blocks forever)
        server.start();

        // Stop the server
        server.stop();
    }

    // Creates a Chain with sample branches and employees
    private static Chain generateChain() {
        Chain chain = new Chain("FashionStore Chain");

        GenerateBranches(chain);

        return chain;
    }

    // Load branch keys dynamically from config.json
    private static void GenerateBranches(Chain chain) {
        Set<String> branchKeys = JsonLoader.getKeys("branches");

        // Iterate over each branch key and create branches dynamically
        for (String branchId : branchKeys) {
            String branchName = JsonLoader.getString("branches." + branchId);
            System.out.println("  Creating branch: " + branchId + " - " + branchName);
            StoreBranch branch = new StoreBranch(branchId, branchName);
            chain.addBranch(branch);
            GenerateEmployees(branch);

        }

        System.out.println("Sample data created: " + branchKeys.size() + " branches loaded.");
        System.out.println();
    }

    private static void GenerateEmployees(StoreBranch branch) {
        Set<String> employeeKeys = JsonLoader.getKeys("employees");

        // Iterate over each employee and add to matching branch
        for (String employeeId : employeeKeys) {
            String basePath = "employees." + employeeId;
            String employeeBranchId = JsonLoader.getString(basePath + ".branchId");

            // Skip if employee doesn't belong to this branch
            if (!employeeBranchId.equals(branch.getBranchId())) {
                continue;
            }

            String employeeName = JsonLoader.getString(basePath + ".fullName");
            String employeePhone = JsonLoader.getString(basePath + ".phone");
            String employeeRole = JsonLoader.getString(basePath + ".role").toUpperCase();
            String employeePassword = JsonLoader.getString(basePath + ".password");

            Employee employee = new Employee(employeeId,
                    employeeName,
                    employeePhone,
                    employeeBranchId,
                    Role.valueOf(employeeRole),
                    employeePassword);

            branch.addEmployee(employee);
            System.out.println("  Created employee: " + employeeName + " (" + employeeId + ") - " + employeeBranchId);
        }

        System.out.println();
    }
}
