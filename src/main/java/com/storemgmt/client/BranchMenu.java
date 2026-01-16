package com.storemgmt.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import com.storemgmt.model.employee.Role;
import com.storemgmt.security.PasswordValidator;
import com.storemgmt.utils.JsonLoader;


public class BranchMenu {

    private BranchClient client;
    private DataInputStream console;
    private String employeeName;
    private String role;
    private volatile boolean waitingForReplyDecision = false;
    private volatile boolean waitingForQueueDecision = false;
    private String pendingQueueId = null; 
    private boolean inChatMode = false;
    private String currentChatPartner = null;

    public BranchMenu(BranchClient client) {
        this.client = client;
        this.console = new DataInputStream(System.in);

        client.setOnChatMessage(() -> {
        if (employeeName != null) {
                System.out.println("\nDo you want to reply? (Y/N):"); 
                waitingForReplyDecision = true;
            }
        });

        client.setOnQueueUpdate((requesterId) -> {
            System.out.println("\n************************************************");
            System.out.println("  ALERT: User " + requesterId + " is waiting for a chat!");
            System.out.println("************************************************");
            System.out.print("Do you want to accept the call? (Y/N): ");
            
            pendingQueueId = requesterId; 
            waitingForQueueDecision = true; 
        });

        client.setOnChatEnded(() -> {
            try {
                Thread.sleep(100); 
            } catch (InterruptedException e) {}
            
            printMenu(); 
            System.out.print("Choose option: "); 
        });
    }
    
    private void printMenu() {
        if ("ADMIN".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role)) {
            printAdminMenu();
        } else {
            printRegularMenu();
        }
    }


    public void run() throws IOException {
        String input;

        while (client.isConnected()) {

            if (employeeName == null && !login()) {
                continue;
            }

            // if ("ADMIN".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role)) {
            //     printAdminMenu();
            // } else {
            //     printRegularMenu();
            // }
            // System.out.print("Choose option: ");
            // String input = console.readLine();

            if (waitingForReplyDecision) {
                waitingForReplyDecision = false;
                if (!inChatMode) {
                    inChatMode = true;
                    System.out.println(">> Press ENTER (empty) to exit chat, or type to reply.");
                }
            }
            
            if (!waitingForQueueDecision) {
                if (!inChatMode) {
                    if ("ADMIN".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role)) {
                        printAdminMenu();
                    } else {
                        printRegularMenu();
                    }
                    System.out.print("Choose option: ");
                } else {
                    System.out.print("[Chat] > ");
                }
            }

            input = console.readLine();

            if (waitingForQueueDecision) {
                waitingForQueueDecision = false; 

                if (input.equalsIgnoreCase("Y")) {
                    System.out.println("Accepting chat from " + pendingQueueId + "...");
                    
                    String msg = "OPEN_CHAT|" + client.getBranchId()
                            + "|from:" + client.getEmployeeId()
                            + "|to:" + pendingQueueId
                            + "|message:I am available now, let's talk.";
                    
                    String[] response = client.send(msg);
                    if (response.length > 1) System.out.println(response[1]);

                    currentChatPartner = pendingQueueId;
                    inChatMode = true; 
                    
                } else if (input.equalsIgnoreCase("N")) {
                    System.out.println("Ignored chat request.");
                } else {
                    System.out.println("Invalid input. Returning to menu...");
                }

                pendingQueueId = null; 
                continue; 
            }

            if (inChatMode) {
                if (input == null || input.trim().isEmpty()) {
                    System.out.println("Exiting chat mode...");
                    inChatMode = false;
                    currentChatPartner = null;
                } 
                else {
                    sendMessageInsideChat(input);
                }
                continue; 
            }


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
                case "9":
                    reportManagement();
                    break;
                case "10":
                    requestChat();
                    break;
                case "11":
                    sendMessage();
                    break;
                case "12":
                    joinActiveChat();
                    break;
                case "0":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option!\n");
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    

    private void reportManagement() throws IOException {
        System.out.println("Choose display type: (1) By Branch, (2) By Category, (3) By Product");
        String input = console.readLine();

        switch (input) {
            case "1":
                showReportByBranch();
                break;
            case "2":
                showReportByCategory();
                break;
            case "3":
                showReportByProduct();
                break;
            default:
                System.out.println("Invalid option!\n");
        }
    }

    private void showReportByBranch() {
        String msg = "SHOW_REPORT_BY_BRANCH|" + client.getBranchId();
        String[] response = client.send(msg);
        if (response[0].equals("OK")) {
            System.out.println(response[1] + "\n" + response[2] + "\n" + response[3]);
        } else {
            System.out.println(response[1]);
        }
    }

    private void joinActiveChat() throws IOException {
        if (!role.contains("MANAGER") && !role.equals("ADMIN")) {
            System.out.println("Access Denied: Only managers can join chats.");
            return;
        }

        String msgList = "GET_ACTIVE_CHATS|" + client.getBranchId();
        String[] response = client.send(msgList);
        
        if (response[0].equals("ERROR")) {
            System.out.println(response[1]);
            return;
        }
        
        HashMap<String, String> data = BranchClient.getData(response);
        System.out.println(response[1]);
        System.out.println(data.get("list").replace("\\n", "\n"));

        System.out.print("Enter the Employee ID of someone in the chat you want to join: ");
        String targetId = console.readLine().toUpperCase();

        String msgJoin = "JOIN_CHAT|" + client.getBranchId() 
                       + "|managerId:" + client.getEmployeeId()
                       + "|targetId:" + targetId;

        String[] joinResponse = client.send(msgJoin);
        
        if (joinResponse[0].equals("OK")) {
            System.out.println("You have joined the chat!");

            // Question for the manager/admin
            System.out.print("\nDo you want to send a message? (Y/N): ");
            String ans = console.readLine();

            if (ans.equalsIgnoreCase("Y")) {
                sendMessage(); 
            }

        } else {
            System.out.println(joinResponse[1]);
        }
    }

    private void showReportByCategory() {
        String msg = "SHOW_REPORT_BY_CATEGORY|" + client.getBranchId();
        String[] response = client.send(msg);
        if (response[0].equals("OK")) {
            System.out.println(response[1]);
        } else {
            System.out.println(response[1]);
        }
    }

    private void showReportByProduct() {
        String msg = "SHOW_REPORT_BY_PRODUCT|" + client.getBranchId();
        String[] response = client.send(msg);
        if (response[0].equals("OK")) {
            System.out.println(response[1]);
        } else {
            System.out.println(response[1]);
        }
    }

    private void updateInventory() throws IOException {
        String productId = readProductId();
        if (productId == null)
            return;

        String quantity = readQuantity();
        if (quantity == null)
            return;

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

    private String readProductId() throws IOException {
        Set<String> existingIds = JsonLoader.getKeys("products");
        while (true) {
            System.out.print("Product ID (must start with 'P'): ");
            String input = console.readLine().toUpperCase();

            if (!input.startsWith("P")) {
                System.out.println("  Product ID must start with 'P'. Please try again.");
                continue;
            }

            if (!existingIds.contains(input)) {
                System.out.println("  Product ID '" + input + "' does not exist. Available: " + existingIds);
                continue;
            }

            return input;
        }
    }

    private String readQuantity() throws IOException {
        while (true) {
            System.out.print("Quantity (positive number): ");
            String input = console.readLine();

            if (!isAllDigits(input)) {
                System.out.println("  Quantity must contain only digits.");
                continue;
            }

            return input;
        }
    }

    private void addEmployee() throws IOException {
        String empId = readEmployeeId();
        String fullName = readName();
        String phone = readPhone();
        String branchId = readBranchId();
        String role = readRole();
        String password = readPassword();

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
        Set<String> existingIds = JsonLoader.getKeys("employees");
        String empId = console.readLine().toUpperCase();

        if (empId.equals(client.getEmployeeId())) {
            System.out.println("You cannot remove yourself.");
            return;
        }

        if (!existingIds.contains(empId)) {
            System.out.println("Employee ID " + empId + " does not exist.");
            return;
        }

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
        System.out.println("2. Sell");
        System.out.println("3. View Employees");
        System.out.println("4. Send Chat");
        System.out.println("5. Logout");
        System.out.println("11. Send Message");
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
        System.out.println("2. Sell");
        System.out.println("3. View Employees");
        System.out.println("4. Send Chat");
        System.out.println("5. Logout");
        System.out.println("6. Add Employee");
        System.out.println("7. Remove Employee");
        System.out.println("8. Update Inventory");
        System.out.println("9. View Sell Report");
        System.out.println("10. Request Chat (find available employee)");
        System.out.println("11. Send Message");
        System.out.println("12. Join Active Chat (Manager Only)");
        System.out.println("0. Exit");
        System.out.println("========================================");
    }

    private boolean login() throws IOException {
        System.out.print("Employee ID: ");
        String empId = console.readLine();
        System.out.print("Password: ");
        String password = console.readLine();

        // Send: LOGIN|branchId|employeeId:xxx|password:xxx
        String msg = "LOGIN|" + client.getBranchId() + "|employeeId:" + empId + "|password:" + password;
        String[] response = client.send(msg);

        if (response[0].equals("OK")) {
            client.setEmployeeId(empId);
            HashMap<String, String> data = BranchClient.getData(response);
            employeeName = data.get("employeeName");
            role = data.get("role");
            System.out.println("\n" + response[1]);
            System.out.println("  Welcome, " + employeeName + "!");
            client.startListening();
            return true;
        } else {
            System.out.println(response[1]);
            return false;
        }
    }

    private void viewInventory() {
        String msg = "GET_INVENTORY|" + client.getBranchId();

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
        if (employeeName == null) {
            System.out.println("Please login first!");
            return;
        }

        // System.out.print("Product type (Jean/LongShirt/Bra/RunningShoe): ");
        // String productType = console.readLine();
        // System.out.print("Brand (NIKE/ADIDAS/LEVIS/BERSHKA/POLO): ");
        // String brand = console.readLine();
        // System.out.print("Quantity: ");
        // String qty = console.readLine();

        String msg = "SELL|" + client.getBranchId();

        String[] response = client.send(msg);
        System.out.println(response[1] + "\n" + response[2] + "\n" + response[3]);
    }

    private void viewEmployees() {
        if (employeeName == null) {
            System.out.println("Please login first!");
            return;
        }

        String msg = "GET_EMPLOYEES|" + client.getBranchId();
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

    private void requestChat() {
        if (employeeName == null) {
            System.out.println("Please login first!");
            return;
        }

        String msg = "REQUEST_CHAT|" + client.getBranchId();
        String[] response = client.send(msg);

        if (response != null && response.length > 1) {
            System.out.println(response[1]);
        } else {
            System.out.println("No response from server");
        }
    }

    private void sendChat() throws IOException {
        if (employeeName == null) {
            System.out.println("Please login first!");
            return;
        }

        System.out.print("Enter target employee ID: ");
        String targetEmployeeId = console.readLine().toUpperCase();

        System.out.print("Enter message: ");
        String message = console.readLine();

        String msg =
            "OPEN_CHAT|" + client.getBranchId()
            + "|from:" + client.getEmployeeId()
            + "|to:" + targetEmployeeId
            + "|message:" + message;

        String[] response = client.send(msg);

        if (response.length > 1) {
            System.out.println(response[1]);
        } else {
            System.out.println(response[0]);
        }
    }


    private void logout() {
        if (employeeName == null) {
            System.out.println("Not logged in!");
            return;
        }

        String msg = "LOGOUT|" + client.getBranchId();
        String[] response = client.send(msg);

        if (response[0].equals("OK")) {
            System.out.println(response[1]);
            employeeName = null;
            role = null;
        } else {
            System.out.println(response[1]);
        }
    }

    // ============ Input Validation  ============

    private String readName() throws IOException {
        while (true) {
            System.out.print("Full name: ");
            String input = console.readLine();
            if (!input.isEmpty() || isAllLetter(input)) {
                return input;
            }
            System.out.println("  This field is required. Please try again.");
        }
    }

    private boolean isAllLetter(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    private String readEmployeeId() throws IOException {
        Set<String> existingIds = JsonLoader.getKeys("employees");
        while (true) {
            System.out.print("Employee ID (must start with 'E'): ");
            String input = console.readLine().toUpperCase();
            if (input.isEmpty() || !input.startsWith("E")) {
                System.out.println("  Employee ID must start with 'E'. Please try again.");
                continue;
            }
            if (existingIds.contains(input)) {
                System.out.println("  Employee ID '" + input + "' already exists. Please choose a different ID.");
                continue;
            }
            return input;
        }
    }

    private String readPhone() throws IOException {
        while (true) {
            System.out.print("Phone (10 digits, starts with 0): ");
            String input = console.readLine();
            if (input.length() == 10 && input.startsWith("0") && isAllDigits(input)) {
                return input;
            }
            System.out.println("  Phone must be 10 digits starting with 0. Please try again.");
        }
    }

    private boolean isAllDigits(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private String readBranchId() throws IOException {
        Set<String> validBranches = JsonLoader.getKeys("branches");
        while (true) {
            System.out.print("Branch ID " + validBranches + ": ");
            String input = console.readLine().toUpperCase();
            if (validBranches.contains(input)) {
                return input;
            }
            System.out.println("  Invalid branch. Choose from: " + validBranches);
        }
    }

    private String readRole() throws IOException {
        System.out.println("  Available roles: CASHIER, SELLER, MANAGER, SHIFT_MANAGER, ADMIN");
        while (true) {
            System.out.print("Role: ");
            String input = console.readLine().toUpperCase();
            try {
                Role.valueOf(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println("  Invalid role. Choose: CASHIER, SELLER, MANAGER, SHIFT_MANAGER, or ADMIN");
            }
        }
    }
    
    private void sendMessage() throws IOException {
        System.out.print("Enter message: ");
        String message = console.readLine();

        String msg =
            "SEND_MESSAGE|" + client.getBranchId()
            + "|from:" + client.getEmployeeId()
            + "|message:" + message;

        String[] response = client.send(msg);

        if (response.length > 1) {
            System.out.println(response[1]);
        }
    }

    private void sendMessageInsideChat(String message) throws IOException {
        
        if (currentChatPartner == null) {
            System.out.print("Enter recipient ID for this chat: ");
            currentChatPartner = console.readLine().toUpperCase();
        }

        String msgPacket = "SEND_CHAT|" + client.getBranchId() + 
                           "|from:" + client.getEmployeeId() + 
                           "|to:" + currentChatPartner +
                           "|message:" + message;
        
        client.send(msgPacket);
    }


    private String readPassword() throws IOException {
        System.out.println("  Password requirements: 8+ chars, uppercase, lowercase, digit");
        while (true) {
            System.out.print("Password: ");
            String input = console.readLine();
            String validationResult = PasswordValidator.validate(input);
            if (validationResult.equals("valid")) {
                return input;
            }
            System.out.println("  " + validationResult + ". Please try again.");
        }
    }

    private void endChat() {
        System.out.println("Closing chat...");
        String msg = "END_CHAT|" + client.getBranchId() 
                   + "|from:" + client.getEmployeeId();
        
        String[] response = client.send(msg);
        if (response.length > 1) {
            System.out.println(response[1]);
        }
    }
}
