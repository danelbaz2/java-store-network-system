package com.storemgmt.server;

import com.storemgmt.model.employee.Employee;
import com.storemgmt.model.employee.Role;
import com.storemgmt.model.store.Chain;
import com.storemgmt.model.store.StoreBranch;
import com.storemgmt.utils.JsonLoader;

import java.nio.file.Paths;
import java.util.Set;

public class ServerApp {
    public static void main(String[] args) {
        JsonLoader.load(Paths.get(System.getProperty("user.dir"), "config.json").toString());
        int port = JsonLoader.getInt("server.port");

        Chain chain = generateChain();

        StoreServer server = new StoreServer(chain, port);

        server.start();

        server.stop();
    }

    private static Chain generateChain() {
        Chain chain = new Chain("FashionStore Chain");

        GenerateBranches(chain);

        return chain;
    }

    private static void GenerateBranches(Chain chain) {
        Set<String> branchKeys = JsonLoader.getKeys("branches");

        for (String branchId : branchKeys) {
            String branchName = JsonLoader.getString("branches." + branchId + ".branchName");
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

        for (String employeeId : employeeKeys) {
            String basePath = "employees." + employeeId;
            String employeeBranchId = JsonLoader.getString(basePath + ".branchId");

            if (!employeeBranchId.equals(branch.getBranchId())) {
                continue;
            }

            String employeeName = JsonLoader.getString(basePath + ".fullName");
            String employeePhone = JsonLoader.getString(basePath + ".phone");
            String employeeRole = JsonLoader.getString(basePath + ".role").toUpperCase();
            String employeePassword = JsonLoader.getString(basePath + ".password");
            String employeeBankAccount = JsonLoader.getString(basePath + ".bankAccount");

            Employee employee = new Employee(employeeId, employeeName, employeePhone, employeeBranchId,
                    Role.valueOf(employeeRole), employeePassword, employeeBankAccount);

            branch.getEmployees().add(employee);
            System.out.println("  Created employee: " + employeeName + " (" + employeeId + ") - " + employeeBranchId);
        }

        System.out.println();
    }
}
