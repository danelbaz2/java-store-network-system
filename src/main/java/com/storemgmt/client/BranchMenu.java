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
            printMenu();
            System.out.print("Choose option: ");
            String input = console.readLine();

            switch (input) {
                case "1":
                    login();
                    break;
                case "2":
                    viewInventory();
                    break;
                case "3":
                    sellProduct();
                    break;
                case "4":
                    viewEmployees();
                    break;
                case "5":
                    sendChat();
                    break;
                case "6":
                    logout();
                    break;
                case "0":
                    client.disconnect();
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option!\n");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n========================================");
        System.out.println("       Branch " + client.getBranchId() + " Menu");
        if (employeeName != null) {
            System.out.println("       Logged in: " + employeeName + " (" + role + ")");
        }
        System.out.println("========================================");
        System.out.println("1. Login");
        System.out.println("2. View Inventory");
        System.out.println("3. Sell Product");
        System.out.println("4. View Employees");
        System.out.println("5. Send Chat");
        System.out.println("6. Logout");
        System.out.println("0. Exit");
        System.out.println("========================================");
    }

    private void login() throws IOException {
        if (sessionId != null) {
            System.out.println("Already logged in as " + employeeName);
            return;
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
        } else {
            System.out.println(response[1]);
        }
    }

    private void viewInventory() {
        String msg = "GET_INVENTORY|" + client.getBranchId();
        if (sessionId != null) {
            msg += "|sessionId:" + sessionId;
        }

        String[] response = client.send(msg);

        if (response[0].equals("OK")) {
            HashMap<String, String> data = BranchClient.getData(response);
            System.out.println(response[1]);
            System.out.println("-----------------------------------");
            System.out.println(data.get("inventory"));
        } else {
            System.out.println(response[1]);
        }
    }

    private void sellProduct() throws IOException {
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

        String msg = "SELL_PRODUCT|" + client.getBranchId() +
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
            System.out.println(data.get("employees"));
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
