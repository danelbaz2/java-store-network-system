package com.storemgmt.client;

import java.io.DataInputStream;
import java.io.IOException;

import com.storemgmt.protocol.ActionType;
import com.storemgmt.protocol.Request;
import com.storemgmt.protocol.Response;

/**
 * Console UI for branch employees.
 * 
 * Provides menu options for:
 * - Login/Logout
 * - View inventory
 * - Sell products
 * - Chat with other branches
 */
public class BranchMenu {

    private final BranchClient client;
    private final DataInputStream consoleStream;
    private String sessionId; // Current session after login
    private String employeeName; // Logged in employee name
    private String role; // Employee role (ADMIN, CASHIER, etc.)

    public BranchMenu(BranchClient client) {
        this.client = client;
        this.consoleStream = new DataInputStream(System.in);
    }

    // Main menu loop
    public void run() throws IOException {

        // Looping while client is connected
        while (client.isConnected()) {
            printMenu();

            System.out.print("Choose option: ");
            String input = consoleStream.readLine();

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
                    sendChatMessage();
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

    // Print the menu
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
        System.out.println("5. Send Chat Message");
        System.out.println("6. Logout");
        System.out.println("0. Exit");
        System.out.println("========================================");
    }

    // Login action
    private void login() throws IOException {

        // Return if already logged in
        if (sessionId != null) {
            System.out.println("Already logged in as " + employeeName);
            return;
        }

        System.out.print("Employee ID: ");
        String employeeId = consoleStream.readLine();

        System.out.print("Password: ");
        String password = consoleStream.readLine();

        // Create and send login request
        Request request = new Request(ActionType.LOGIN, client.getBranchId())
                .put("employeeId", employeeId)
                .put("password", password);

        Response response = client.sendRequest(request);

        if (response.isSuccess()) {
            sessionId = response.getString("sessionId");
            employeeName = response.getString("employeeName");
            role = response.getString("role");
            System.out.println();
            System.out.println(response.getMessage());
            System.out.println("  Welcome, " + employeeName + "!");
        } else {
            System.out.println(response.getMessage());
        }
    }

    // View inventory
    private void viewInventory() {
        Request request = new Request(ActionType.GET_INVENTORY, client.getBranchId());
        if (sessionId != null) {
            request.setSessionId(sessionId);
        }

        Response response = client.sendRequest(request);

        if (response.isSuccess()) {
            System.out.println(response.getMessage());
            System.out.println("─────────────────────────────────────");
            System.out.println(response.getString("inventory"));
        } else {
            System.out.println(response.getMessage());
        }
    }

    // Sell a product
    private void sellProduct() throws IOException {
        if (sessionId == null) {
            System.out.println("Please login first!");
            return;
        }

        System.out.print("Product type (Jean/LongShirt/Bra/RunningShoe): ");
        String productType = consoleStream.readLine();

        System.out.print("Brand (NIKE/ADIDAS/LEVIS/BERSHKA/POLO): ");
        String brand = consoleStream.readLine();

        System.out.print("Quantity: ");
        String quantity = consoleStream.readLine();

        Request request = new Request(ActionType.SELL_PRODUCT, client.getBranchId())
                .put("productType", productType)
                .put("brand", brand)
                .put("quantity", quantity);
        request.setSessionId(sessionId);

        Response response = client.sendRequest(request);

        if (response.isSuccess()) {
            System.out.println(response.getMessage());
        } else {
            System.out.println(response.getMessage());
        }
    }

    // View employees
    private void viewEmployees() {
        if (sessionId == null) {
            System.out.println("Please login first!");
            return;
        }

        Request request = new Request(ActionType.GET_EMPLOYEES, client.getBranchId());
        request.setSessionId(sessionId);

        Response response = client.sendRequest(request);

        if (response.isSuccess()) {
            System.out.println(response.getMessage());
            System.out.println("─────────────────────────────────────");
            System.out.println(response.getString("employees"));
        } else {
            System.out.println(response.getMessage());
        }
    }

    // Send chat message
    private void sendChatMessage() throws IOException {
        if (sessionId == null) {
            System.out.println("Please login first!");
            return;
        }

        System.out.print("Enter message: ");
        String message = consoleStream.readLine();

        Request request = new Request(ActionType.SEND_MESSAGE, client.getBranchId())
                .put("message", message);
        request.setSessionId(sessionId);

        Response response = client.sendRequest(request);
        System.out.println(response.isSuccess() ? response.getMessage() : response.getMessage());
    }

    // Logout
    private void logout() {
        if (sessionId == null) {
            System.out.println("Not logged in!");
            return;
        }

        Request request = new Request(ActionType.LOGOUT, client.getBranchId());
        request.setSessionId(sessionId);

        Response response = client.sendRequest(request);

        if (response.isSuccess()) {
            System.out.println(response.getMessage());
            sessionId = null;
            employeeName = null;
            role = null;
        } else {
            System.out.println(response.getMessage());
        }
    }
}
