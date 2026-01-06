package com.storemgmt.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

public class BranchMenu {

    private BranchClient client;
    private DataInputStream console;
    private String sessionId;
    private String employeeName;
    private String role;

    public BranchMenu(BranchClient client) {
        this.client = client;
        this.console = new DataInputStream(System.in);
    }

    public void run() throws IOException {

        while (client.isConnected()) {

            if (!login()) {
                continue;
            }

            if (role.equals("ADMIN")) {
                printAdminMenu();
            } else {
                printRegularMenu();
            }
            System.out.print("Choose option: ");
            String input = console.readLine();

            switch (input) {
                case "1":
                    viewInventory();
                    break;
                case "2":
                    sell();
                    break;
                case "3":
                    viewEmployees();
                    break;
                case "4":
                    sendChat();
                    break;
                case "5":
                    logout();
                    break;
                case "6":
                    addEmployee();
                    break;
                case "7":
                    removeEmployee();
                    break;
                case "8":
                    updateInventory();
                    break;
                case "0":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option!\n");
            }
        }
    }

    private void updateInventory() throws IOException {
        System.out.print("Product ID: ");
        String productId = console.readLine();
        System.out.print("Quantity: ");
        String quantity = console.readLine();

        String msg = "UPDATE_INVENTORY|"
                + client.getBranchId()
                + "|productId:" + productId
                + "|quantity:" + quantity;

        String[] response = client.send(msg);

        if (response[0].equals("OK")) {
            System.out.println("Inventory updated successfully!");
        } else {
            System.out.println(response[1]);
        }
    }

    private void addEmployee() throws IOException {
        System.out.print("Employee ID: ");
        String empId = console.readLine();
        System.out.print("Full name: ");
        String fullName = console.readLine();
        System.out.print("Phone: ");
        String phone = console.readLine();
        System.out.print("Branch ID: ");
        String branchId = console.readLine();
        System.out.print("Role: ");
        String role = console.readLine();
        System.out.print("Password: ");
        String password = console.readLine();

        String msg = "ADD_EMPLOYEE|"
                + client.getBranchId()
                + "|employeeId:" + empId
                + "|fullName:" + fullName
                + "|phone:" + phone
                + "|branchId:" + branchId
                + "|role:" + role
                + "|password:" + password;

        String[] response = client.send(msg);

        if (response[0].equals("OK")) {
            System.out.println("Employee added successfully!");
        } else {
            System.out.println(response[1]);
        }
    }

    private void removeEmployee() throws IOException {
        System.out.println("Enter the EMPLOYEE ID: ");
        String empId = console.readLine();

        String msg = "REMOVE_EMPLOYEE|"
                + client.getBranchId()
                + "|employeeId:" + empId;

        String[] response = client.send(msg);

        if (response[0].equals("OK")) {
            System.out.println("Employee removed successfully!");
        } else {
            System.out.println(response[1]);
        }
    }

    private void printRegularMenu() {
        System.out.println("\n========================================");
        System.out.println("       Branch " + client.getBranchId() + " Menu");
        if (employeeName != null) {
            System.out.println("       Logged in: " + employeeName + " (" + role + ")");
        }
        System.out.println("========================================");
        System.out.println("1. View Inventory");
        // System.out.println("2. Sell");
        System.out.println("3. View Employees");
        System.out.println("4. Send Chat");
        System.out.println("5. Logout");
        System.out.println("0. Exit");
        System.out.println("========================================");
    }

    private void printAdminMenu() {
        System.out.println("\n========================================");
        System.out.println("       Branch " + client.getBranchId() + " Menu");
        if (employeeName != null) {
            System.out.println("       Logged in: " + employeeName + " (" + role + ")");
        }
        System.out.println("========================================");
        System.out.println("1. View Inventory");
        // System.out.println("2. Sell");
        System.out.println("3. View Employees");
        System.out.println("4. Send Chat");
        System.out.println("5. Logout");
        System.out.println("6. Add Employee");
        System.out.println("7. Remove Employee");
        System.out.println("8. Update Inventory");
        System.out.println("0. Exit");
        System.out.println("========================================");
    }

    private boolean login() throws IOException {
        if (sessionId != null) {
            System.out.println("Already logged in as " + employeeName);
            return false;
        }

        System.out.print("Employee ID: ");
        String empId = console.readLine();
        System.out.print("Password: ");
        String password = console.readLine();

        // Send: LOGIN|branchId|employeeId:xxx|password:xxx
        String msg = "LOGIN|" + client.getBranchId() + "|employeeId:" + empId + "|password:" + password;
        String[] response = client.send(msg);

        if (response[0].equals("OK")) {
            HashMap<String, String> data = BranchClient.getData(response);
            sessionId = data.get("sessionId");
            employeeName = data.get("employeeName");
            role = data.get("role");
            System.out.println("\n" + response[1]);
            System.out.println("  Welcome, " + employeeName + "!");
            return true;
        } else {
            System.out.println(response[1]);
            return false;
        }
    }

    private void viewInventory() {
        String msg = "GET_INVENTORY|" + client.getBranchId();
        if (sessionId != null) {
            msg += "|sessionId:" + sessionId;
        }

        String[] response = client.send(msg);

        if (response == null || response.length == 0) {
            System.out.println("No response from server");
            return;
        }

        if (response[0].equals("OK") && response.length > 1) {
            HashMap<String, String> data = BranchClient.getData(response);
            System.out.println(response[1]);
            System.out.println("-----------------------------------");

            String inventory = data.get("inventory");
            if (inventory != null) {
                System.out.println(inventory.replace("\\n", "\n")); // Replace escaped newlines with real newlines
            }
        } else if (response.length > 1) {
            System.out.println(response[1]);
        } else {
            System.out.println("Invalid response from server");
        }
    }

    private void sell() throws IOException {
        if (sessionId == null) {
            System.out.println("Please login first!");
            return;
        }

        System.out.print("Product type (Jean/LongShirt/Bra/RunningShoe): ");
        String productType = console.readLine();
        System.out.print("Brand (NIKE/ADIDAS/LEVIS/BERSHKA/POLO): ");
        String brand = console.readLine();
        System.out.print("Quantity: ");
        String qty = console.readLine();

        String msg = "SELL|" + client.getBranchId() +
                "|sessionId:" + sessionId +
                "|productType:" + productType +
                "|brand:" + brand +
                "|quantity:" + qty;

        String[] response = client.send(msg);
        System.out.println(response[1]);
    }

    private void viewEmployees() {
        if (sessionId == null) {
            System.out.println("Please login first!");
            return;
        }

        String msg = "GET_EMPLOYEES|" + client.getBranchId() + "|sessionId:" + sessionId;
        String[] response = client.send(msg);

        if (response[0].equals("OK")) {
            HashMap<String, String> data = BranchClient.getData(response);
            System.out.println(response[1]);
            System.out.println("-----------------------------------");
            String employees = data.get("employees");
            if (employees != null) {
                System.out.println(employees.replace("\\n", "\n")); // Replace escaped newlines with real newlines
            }
        } else {
            System.out.println(response[1]);
        }
    }

    private void sendChat() throws IOException {
        if (sessionId == null) {
            System.out.println("Please login first!");
            return;
        }

        System.out.print("Enter message: ");
        String message = console.readLine();

        String msg = "SEND_MESSAGE|" + client.getBranchId() + "|sessionId:" + sessionId + "|message:" + message;
        String[] response = client.send(msg);
        System.out.println(response[1]);
    }

    private void logout() {
        if (sessionId == null) {
            System.out.println("Not logged in!");
            return;
        }

        String msg = "LOGOUT|" + client.getBranchId() + "|sessionId:" + sessionId;
        String[] response = client.send(msg);

        if (response[0].equals("OK")) {
            System.out.println(response[1]);
            sessionId = null;
            employeeName = null;
            role = null;
        } else {
            System.out.println(response[1]);
        }
    }
}
