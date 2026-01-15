package com.storemgmt.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import com.storemgmt.model.employee.Role;
import com.storemgmt.security.PasswordValidator;
import com.storemgmt.utils.JsonLoader;

@SuppressWarnings("deprecation")
public class BranchMenu implements BranchClient.ChatCallback {

    private BranchClient client;
    private DataInputStream console;
    private String employeeName;
    private String role;

    private boolean pendingChatRequest = false;
    private String pendingChatFromId = null;
    private String pendingChatFromName = null;
    private boolean chatEnded = false;

    public BranchMenu(BranchClient client) {
        this.client = client;
        this.console = new DataInputStream(System.in);
        this.client.setChatCallback(this);
    }

    @Override
    public void onChatRequest(String fromEmployeeId, String fromName) {
        pendingChatRequest = true;
        pendingChatFromId = fromEmployeeId;
        pendingChatFromName = fromName;
        System.out.println("\n\n==================================================");
        System.out.println("  *** INCOMING CHAT REQUEST from " + fromName + " (" + fromEmployeeId + ") ***");
        System.out.println("==================================================");
        System.out.println("Type your messages below. Type 'exit' to end chat.\n");
    }

    @Override
    public void onChatMessage(String fromEmployeeId, String message) {
        System.out.println(fromEmployeeId + ": " + message);
    }

    @Override
    public void onChatEnded(String byEmployeeId) {
        chatEnded = true;
        System.out.println("\n*** Chat ended by " + byEmployeeId + " ***");
        System.out.println("Press Enter to return to menu...");
    }

    @Override
    public void onAvailabilityNotification(String targetEmployeeId) {
        System.out.println("\n*** " + targetEmployeeId + " is now available for chat! ***\n");
    }

    public void run() throws IOException {

        while (client.isConnected()) {

            if (pendingChatRequest) {
                handleIncomingChatSession(null);
                continue;
            }

            if (employeeName == null && !login()) {
                continue;
            }

            if (role.equals("ADMIN")) {
                printAdminMenu();
            } else if (role.equals("MANAGER")) {
                printManagerMenu();
            } else {
                printRegularMenu();
            }
            System.out.print("Choose option: ");
            String input = console.readLine();

            if (pendingChatRequest) {
                handleIncomingChatSession(input);
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
                    startChat();
                    break;
                case "5":
                    logout();
                    break;
                case "6":
                    if (role.equals("MANAGER")) {
                        monitorChats();
                    } else {
                        addEmployee();
                    }
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
                case "0":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option!\n");
            }
        }
    }

    private void handleIncomingChatSession(String firstMessage) throws IOException {
        String partnerId = pendingChatFromId;

        pendingChatRequest = false;
        pendingChatFromId = null;
        pendingChatFromName = null;
        chatEnded = false;

        client.setInChatMode(true);
        client.setChatPartnerId(partnerId);

        String acceptMsg = "ACCEPT_CHAT|" + client.getBranchId() + "|targetEmployeeId:" + partnerId;
        client.send(acceptMsg);

        if (firstMessage != null && !firstMessage.isEmpty() && !firstMessage.equalsIgnoreCase("exit")) {
            String sendMsg = "SEND_CHAT_MESSAGE|" + client.getBranchId()
                    + "|targetEmployeeId:" + partnerId
                    + "|message:" + firstMessage;
            String[] sendResponse = client.send(sendMsg);
            if (!sendResponse[0].equals("OK")) {
                System.out.println("Failed to send: " + (sendResponse.length > 1 ? sendResponse[1] : "Unknown error"));
            }
        }

        while (!chatEnded && client.isConnected()) {
            String chatMessage = console.readLine();

            if (chatMessage == null || chatMessage.equalsIgnoreCase("exit")) {
                String endMsg = "END_CHAT|" + client.getBranchId() + "|targetEmployeeId:" + partnerId;
                client.send(endMsg);
                System.out.println("Chat ended.");
                break;
            }

            String sendMsg = "SEND_CHAT_MESSAGE|" + client.getBranchId()
                    + "|targetEmployeeId:" + partnerId
                    + "|message:" + chatMessage;
            String[] sendResponse = client.send(sendMsg);

            if (!sendResponse[0].equals("OK")) {
                System.out.println("Failed to send: " + (sendResponse.length > 1 ? sendResponse[1] : "Unknown error"));
                break;
            }
        }

        client.setInChatMode(false);
        client.setChatPartnerId(null);
        chatEnded = false;
        System.out.println("\nReturning to main menu...\n");
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
        printResponse(response);

    }

    private void showReportByCategory() {
        String msg = "SHOW_REPORT_BY_CATEGORY|" + client.getBranchId();
        String[] response = client.send(msg);
        printResponse(response);
    }

    private void showReportByProduct() {
        String msg = "SHOW_REPORT_BY_PRODUCT|" + client.getBranchId();
        String[] response = client.send(msg);
        printResponse(response);
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
        printResponse(response);
    }

    private String readProductId() throws IOException {
        Set<String> existingIds = JsonLoader.getKeys("productCatalog");
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
        System.out.println("4. Start Chat");
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
        System.out.println("2. Sell");
        System.out.println("3. View Employees");
        System.out.println("4. Start Chat");
        System.out.println("5. Logout");
        System.out.println("6. Add Employee");
        System.out.println("7. Remove Employee");
        System.out.println("8. Update Inventory");
        System.out.println("9. View Sell Report");
        System.out.println("0. Exit");
        System.out.println("========================================");
    }

    private void printManagerMenu() {
        System.out.println("\n========================================");
        System.out.println("       Branch " + client.getBranchId() + " Menu");
        if (employeeName != null) {
            System.out.println("       Logged in: " + employeeName + " (" + role + ")");
        }
        System.out.println("========================================");
        System.out.println("1. View Inventory");
        System.out.println("2. Sell");
        System.out.println("3. View Employees");
        System.out.println("4. Start Chat");
        System.out.println("5. Logout");
        System.out.println("6. Monitor Chats");
        System.out.println("0. Exit");
        System.out.println("========================================");
    }

    private boolean login() throws IOException {
        System.out.print("Employee ID: ");
        String empId = console.readLine();
        System.out.print("Password: ");
        String password = console.readLine();

        String msg = "LOGIN|" + client.getBranchId() + "|employeeId:" + empId + "|password:" + password;
        String[] response = client.send(msg);

        if (response[0].equals("OK")) {
            client.setEmployeeId(empId);
            HashMap<String, String> data = BranchClient.getData(response);
            employeeName = data.get("employeeName");
            role = data.get("role");
            System.out.println("\n" + response[1]);
            System.out.println("  Welcome, " + employeeName + "!");
            return true;
        } else {
            printResponse(response);
            return false;
        }
    }

    private void viewInventory() {
        String msg = "GET_INVENTORY|" + client.getBranchId();
        String[] response = client.send(msg);
        printResponse(response);
    }

    private void sell() throws IOException {
        if (employeeName == null) {
            System.out.println("Please login first!");
            return;
        }

        String msg = "SELL|" + client.getBranchId();

        String[] response = client.send(msg);
        printResponse(response);
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
                System.out.println(employees.replace("\\n", "\n"));
            }
        } else {
            printResponse(response);
        }
    }

    private void startChat() throws IOException {
        if (employeeName == null) {
            System.out.println("Please login first!");
            return;
        }

        String msg = "GET_OTHER_BRANCH_EMPLOYEES|" + client.getBranchId();
        String[] response = client.send(msg);

        if (!response[0].equals("OK")) {
            printResponse(response);
            return;
        }

        HashMap<String, String> data = BranchClient.getData(response);
        String employeeList = data.get("employees");

        if (employeeList == null || employeeList.isEmpty()) {
            System.out.println("No employees from other branches are currently connected.");
            return;
        }

        System.out.println("\n--- Connected Employees from Other Branches ---");
        System.out.println(employeeList.replace("\\n", "\n"));
        System.out.println("------------------------------------------------");

        System.out.print("Enter Employee ID to chat with (or 'cancel' to go back): ");
        String targetEmpId = console.readLine().toUpperCase();

        if (targetEmpId.equals("CANCEL")) {
            System.out.println("Chat cancelled.");
            return;
        }

        String startMsg = "START_CHAT|" + client.getBranchId() + "|targetEmployeeId:" + targetEmpId;
        String[] startResponse = client.send(startMsg);

        if (startResponse[0].equals("PENDING")) {
            printResponse(startResponse);
            return;
        }

        if (!startResponse[0].equals("OK")) {
            printResponse(startResponse);
            return;
        }

        client.setInChatMode(true);
        client.setChatPartnerId(targetEmpId);
        chatEnded = false;

        System.out.println("\n==================================================");
        System.out.println("       Chat started with " + targetEmpId);
        System.out.println("==================================================");
        System.out.println("Type your messages. Type 'exit' to end chat.\n");

        while (!chatEnded && client.isConnected()) {
            String chatMessage = console.readLine();

            if (chatMessage == null || chatMessage.equalsIgnoreCase("exit")) {
                String endMsg = "END_CHAT|" + client.getBranchId() + "|targetEmployeeId:" + targetEmpId;
                client.send(endMsg);
                System.out.println("Chat ended.");
                break;
            }

            String sendMsg = "SEND_CHAT_MESSAGE|" + client.getBranchId()
                    + "|targetEmployeeId:" + targetEmpId
                    + "|message:" + chatMessage;
            String[] sendResponse = client.send(sendMsg);

            if (!sendResponse[0].equals("OK")) {
                System.out.println("Failed to send: " + (sendResponse.length > 1 ? sendResponse[1] : "Unknown error"));
                break;
            }
        }

        client.setInChatMode(false);
        client.setChatPartnerId(null);
        chatEnded = false;
        System.out.println("\nReturning to main menu...\n");
    }

    private void monitorChats() throws IOException {
        if (employeeName == null) {
            System.out.println("Please login first!");
            return;
        }

        String msg = "GET_ACTIVE_CHATS|" + client.getBranchId();
        String[] response = client.send(msg);

        if (!response[0].equals("OK")) {
            printResponse(response);
            return;
        }

        HashMap<String, String> data = BranchClient.getData(response);
        String chatList = data.get("chats");

        if (chatList == null || chatList.isEmpty()) {
            System.out.println("No active chat sessions found.");
            return;
        }

        System.out.println("\n--- Active Chat Sessions ---");
        System.out.println(chatList.replace("\\n", "\n"));
        System.out.println("----------------------------");

        System.out.print("Enter Employee ID to join their chat (or 'cancel' to go back): ");
        String targetEmpId = console.readLine().toUpperCase();

        if (targetEmpId.equals("CANCEL")) {
            System.out.println("Cancelled.");
            return;
        }

        String joinMsg = "JOIN_CHAT|" + client.getBranchId() + "|targetEmployeeId:" + targetEmpId;
        String[] joinResponse = client.send(joinMsg);

        if (!joinResponse[0].equals("OK")) {
            printResponse(joinResponse);
            return;
        }

        HashMap<String, String> joinData = BranchClient.getData(joinResponse);
        String chatPartnerId = joinData.get("chatPartnerId");

        client.setInChatMode(true);
        client.setChatPartnerId(targetEmpId);
        chatEnded = false;

        System.out.println("\n==================================================");
        System.out.println("  Joined chat between " + targetEmpId + " and " + chatPartnerId);
        System.out.println("==================================================");
        System.out.println("You can now send messages. Type 'exit' to leave.\n");

        while (!chatEnded && client.isConnected()) {
            String chatMessage = console.readLine();

            if (chatMessage == null || chatMessage.equalsIgnoreCase("exit")) {
                String leaveMsg = "LEAVE_CHAT|" + client.getBranchId()
                        + "|targetEmployeeId:" + targetEmpId
                        + "|chatPartnerId:" + chatPartnerId;
                client.send(leaveMsg);
                System.out.println("Left the chat.");
                break;
            }

            String sendMsg = "ADMIN_CHAT_MESSAGE|" + client.getBranchId()
                    + "|targetEmployeeId:" + targetEmpId
                    + "|chatPartnerId:" + chatPartnerId
                    + "|message:" + chatMessage;
            String[] sendResponse = client.send(sendMsg);

            if (!sendResponse[0].equals("OK")) {
                System.out.println("Failed to send: " + (sendResponse.length > 1 ? sendResponse[1] : "Unknown error"));
                break;
            }
        }

        client.setInChatMode(false);
        client.setChatPartnerId(null);
        chatEnded = false;
        System.out.println("\nReturning to main menu...\n");
    }

    private void logout() {
        if (employeeName == null) {
            System.out.println("Not logged in!");
            return;
        }

        String msg = "LOGOUT|" + client.getBranchId();
        String[] response = client.send(msg);
        printResponse(response);

        if (response[0].equals("OK")) {
            employeeName = null;
            role = null;
        }
    }

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

    private void printResponse(String[] response) {
        if (response == null || response.length == 0) {
            System.out.println("No response from server");
            return;
        }

        if (response.length == 1) {
            System.out.println(response[0]);
            return;
        }

        if (response.length == 2) {
            System.out.println(response[1]);
            return;
        }

        for (int i = 1; i < response.length; i++)
            System.out.println(response[i].replace("\\n", "\n"));
    }
}
