package com.storemgmt.server;

import com.storemgmt.model.employee.Employee;
import com.storemgmt.model.employee.Role;
import com.storemgmt.model.store.Chain;
import com.storemgmt.model.store.StoreBranch;

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
        int port = 5000;

        // Create the chain (central data store)
        Chain chain = createChainWithSampleData();

        // Create and start the server
        StoreServer server = new StoreServer(chain, port);

        // Add shutdown hook for clean exit
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("\nShutting down server...");
                server.stop();
            }
        }));

        // Start the server (blocks forever)
        server.start();
    }

    // Creates a Chain with sample branches and employees
    private static Chain createChainWithSampleData() {
        Chain chain = new Chain("FashionStore Chain");

        // Create branch 1: Tel Aviv
        StoreBranch telAviv = new StoreBranch("BR01", "Tel Aviv");
        telAviv.addEmployee(new Employee("E001", "Admin User", "050-1111111", "BR01", Role.ADMIN, "Admin123!"));
        telAviv.addEmployee(new Employee("E002", "Sarah Cohen", "050-2222222", "BR01", Role.CASHIER, "Cashier1!"));
        telAviv.addEmployee(new Employee("E003", "David Levi", "050-3333333", "BR01", Role.SELLER, "Saeller123!"));
        chain.addBranch(telAviv);

        // Create branch 2: Jerusalem
        StoreBranch jerusalem = new StoreBranch("BR02", "Jerusalem");
        jerusalem.addEmployee(new Employee("E004", "Manager Moshe", "052-4444444", "BR02", Role.ADMIN, "Admin456!"));
        jerusalem.addEmployee(new Employee("E005", "Yael Gold", "052-5555555", "BR02", Role.CASHIER, "Cashier2!"));
        chain.addBranch(jerusalem);

        System.out.println("Sample data created:");
        System.out.println("  Branch: " + telAviv.getName() + " (" + telAviv.getBranchId() + ") - "
                + telAviv.getEmployees().size() + " employees");
        System.out.println("  Branch: " + jerusalem.getName() + " (" + jerusalem.getBranchId() + ") - "
                + jerusalem.getEmployees().size() + " employees");
        System.out.println();

        return chain;
    }
}
