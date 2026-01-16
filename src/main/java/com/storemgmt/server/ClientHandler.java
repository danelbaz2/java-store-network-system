package com.storemgmt.server;

import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.storemgmt.model.customer.Customer;
import com.storemgmt.model.employee.Employee;
import com.storemgmt.model.employee.Role;
import com.storemgmt.model.product.Product;
import com.storemgmt.model.store.Chain;
import com.storemgmt.model.store.StoreBranch;
import com.storemgmt.utils.JsonLoader;
import com.storemgmt.utils.Logger;

@SuppressWarnings("deprecation")
public class ClientHandler implements Runnable {

    private Socket socket;
    private Chain chain;
    private StoreServer server;

    private DataInputStream in;
    private PrintStream out;

    private String branchId;
    private Employee currentEmployee;

    private boolean isAvailableForChat = true;
    private String chatPartnerId = null;

    private static final Map<String, List<String>> chatObservers = new HashMap<>();

    private static String getChatKey(String emp1, String emp2) {
        return emp1.compareTo(emp2) < 0 ? emp1 + ":" + emp2 : emp2 + ":" + emp1;
    }

    public ClientHandler(Socket socket, Chain chain, StoreServer server) {
        this.socket = socket;
        this.chain = chain;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new PrintStream(socket.getOutputStream());

            System.out.println("Client connected, waiting for messages...");

            while (true) {
                String line = in.readLine();
                if (line == null)
                    break;

                System.out.println("Received: " + line);

                String[] parts = line.split("\\|");
                String action = parts[0];
                branchId = parts[1];

                HashMap<String, String> data = new HashMap<>();
                for (int i = 2; i < parts.length; i++) {
                    String[] kv = parts[i].split(":", 2);
                    if (kv.length == 2) {
                        data.put(kv[0], kv[1]);
                    }
                }

                String response = handleAction(action, data);
                out.println(response);
                out.flush();
            }

        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private String handleAction(String action, HashMap<String, String> data) {
        switch (action) {
            case "LOGIN":
                return handleLogin(data);
            case "LOGOUT":
                return handleLogout();
            case "GET_INVENTORY":
                return handleGetInventory();
            case "SELL":
                return handleSell();
            case "GET_EMPLOYEES":
                return handleGetEmployees();
            case "SEND_MESSAGE":
                return handleSendMessage(data);
            case "ADD_EMPLOYEE":
                return handleAddEmployee(data);
            case "REMOVE_EMPLOYEE":
                return handleRemoveEmployee(data);
            case "UPDATE_INVENTORY":
                return handleUpdateInventory(data);
            case "SHOW_REPORT_BY_BRANCH":
                return handleShowReport("branch");
            case "SHOW_REPORT_BY_CATEGORY":
                return handleShowReport("category");
            case "SHOW_REPORT_BY_PRODUCT":
                return handleShowReport("product");
            case "GET_OTHER_BRANCH_EMPLOYEES":
                return handleGetOtherBranchEmployees();
            case "CHECK_CHAT_AVAILABILITY":
                return handleCheckChatAvailability(data);
            case "START_CHAT":
                return handleStartChat(data);
            case "ACCEPT_CHAT":
                return handleAcceptChat(data);
            case "END_CHAT":
                return handleEndChat(data);
            case "SEND_CHAT_MESSAGE":
                return handleSendChatMessage(data);
            case "GET_ACTIVE_CHATS":
                return handleGetActiveChats();
            case "JOIN_CHAT":
                return handleJoinChat(data);
            case "LEAVE_CHAT":
                return handleLeaveChat(data);
            case "ADMIN_CHAT_MESSAGE":
                return handleAdminChatMessage(data);
            case "VIEW_CUSTOMERS":
                return handleViewCustomers();
            default:
                return "ERROR|Unknown action: " + action;
        }
    }

    private String handleUpdateInventory(HashMap<String, String> data) {
        String productId = data.get("productId");
        String quantity = data.get("quantity");

        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return "ERROR|Branch not found: " + branchId;
            }

            Product product = branch.getProductById(productId);
            if (product == null) {
                return "ERROR|Product not found: " + productId;
            }

            branch.getProducts().put(product, Integer.parseInt(quantity));
            JsonLoader.set("branches." + branchId + ".inventory." + productId + ".stock", Integer.parseInt(quantity));
            JsonLoader.save();
            return "OK|Inventory updated successfully";
        }
    }

    private String handleShowReport(String type) {
        try {
            synchronized (chain) {
                java.nio.file.Path reportsDir = Paths.get(System.getProperty("user.dir"), "reports");
                java.nio.file.Files.createDirectories(reportsDir);

                String jsonFilename = Paths.get(System.getProperty("user.dir"), "reports",
                        "report_" + LocalDate.now().toString() + "_by_" + type + ".json").toString();
                FileWriter jsonWriter = new FileWriter(jsonFilename);

                String wordFilename = Paths.get(System.getProperty("user.dir"), "reports",
                        "report_" + LocalDate.now().toString() + "_by_" + type + ".doc").toString();
                FileWriter wordWriter = new FileWriter(wordFilename);

                wordWriter.write("========================================\n");
                wordWriter.write("        " + type.toUpperCase() + " SALES REPORT\n");
                wordWriter.write("========================================\n\n");

                StringBuilder returnString = new StringBuilder("OK|");
                boolean firstBranch = true;

                for (StoreBranch branch : chain.getBranches()) {
                    if (firstBranch) {
                        firstBranch = false;
                        jsonWriter.append("[\n");
                    } else {
                        jsonWriter.append(",\n");
                    }

                    String branchReport;

                    if (type == "category")
                        branchReport = branch.getReportByCategory(jsonWriter);
                    else if (type == "product")
                        branchReport = branch.getReportByProduct(jsonWriter);
                    else
                        branchReport = branch.getReportByBranch(jsonWriter);

                    returnString.append(branchReport).append("|");

                    wordWriter.write("Branch: " + branch.getName() + "\n");
                    wordWriter.write("----------------------------------------\n");
                    String[] parts = branchReport.split("\\|");
                    for (String part : parts) {
                        if (!part.startsWith("CATEGORY REPORT:")) {
                            wordWriter.write("  • " + part + "\n");
                        }
                    }

                    wordWriter.write("\n");
                }

                jsonWriter.append("\n]");
                jsonWriter.close();

                wordWriter.write("========================================\n");
                wordWriter.write("         END OF REPORT\n");
                wordWriter.write("========================================\n");
                wordWriter.close();

                System.out.println("JSON report: " + jsonFilename);
                System.out.println("Word report: " + wordFilename);
                return returnString.toString();
            }
        } catch (Exception e) {
            return "ERROR|" + e.getMessage();
        }
    }

    private String handleAddEmployee(HashMap<String, String> data) {

        isAvailableForChat = false;
        String empId = data.get("employeeId");
        String fullName = data.get("fullName");
        String phone = data.get("phone");
        String branchId = data.get("branchId");
        String role = data.get("role");
        String password = data.get("password");
        String bankAccount = data.get("bankAccount");

        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return "ERROR|Branch not found: " + branchId;
            }

            Employee emp = new Employee(empId, fullName, phone, branchId, Role.valueOf(role), password, bankAccount);
            branch.addEmployee(emp);
            Logger.write("Employee added: " + empId);
            isAvailableForChat = true;
            return "OK|Employee added successfully";
        }
    }

    private String handleRemoveEmployee(HashMap<String, String> data) {
        isAvailableForChat = false;
        String empId = data.get("employeeId");

        Set<String> existingIds = JsonLoader.getKeys("employees");
        if (!existingIds.contains(empId))
            return "ERROR|Employee not found: " + empId;

        String branchId = JsonLoader.getString("employees." + empId + ".branchId");

        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return "ERROR|Branch not found: " + branchId;
            }

            Employee emp = branch.getEmployeeById(empId);
            branch.removeEmployee(emp);
            Logger.write("Employee removed: " + empId);
            isAvailableForChat = true;
            return "OK|Employee removed successfully";
        }
    }

    private String handleLogin(HashMap<String, String> data) {
        String empId = data.get("employeeId");
        String password = data.get("password");

        for (ClientHandler client : server.getConnectedClients()) {
            if (client == this)
                continue;

            if (client.currentEmployee == null)
                continue;

            if (client.currentEmployee.getEmployeeId().equals(empId)) {
                return "ERROR|Already logged in";
            }
        }

        if (empId.isEmpty() || password.isEmpty()) {
            return "ERROR|Missing employeeId or password";
        }

        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return "ERROR|Branch not found: " + branchId;
            }

            for (Employee emp : branch.getEmployees()) {
                if (emp.getEmployeeId().equals(empId)) {
                    if (emp.getPassword().equals(password)) {
                        currentEmployee = emp;

                        return "OK|Login successful" +
                                "|employeeName:" + emp.getFullName() +
                                "|role:" + emp.getRole();
                    } else {
                        return "ERROR|Invalid password";
                    }
                }
            }
            return "ERROR|Employee " + empId + " not found in branch " + branchId;
        }
    }

    private String handleLogout() {
        if (currentEmployee == null) {
            return "ERROR|Not logged in";
        }

        String name = currentEmployee.getFullName();
        currentEmployee = null;
        return "OK|Goodbye, " + name;
    }

    private String handleGetInventory() {
        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return "ERROR|Branch not found";
            }

            StringBuilder inventory = new StringBuilder();
            for (Map.Entry<Product, Integer> entry : branch.getProducts().entrySet()) {
                Product p = entry.getKey();
                int qty = entry.getValue();
                {
                    inventory.append(p.getProductId())
                            .append(" - ").append(p.getBrand())
                            .append(" ")
                            .append(p.getClass().getSimpleName())
                            .append(" (").append(qty)
                            .append(" units) - $")
                            .append(p.getPrice())
                            .append("\\n");
                }
            }

            return "OK|Inventory for " + branch.getName() + "|" + inventory.toString();
        }
    }

    private String handleSell() {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in to sell";
        }

        StoreBranch branch = chain.getBranchById(branchId);
        if (branch == null) {
            return "ERROR|Branch not found";
        }

        String cashierId = currentEmployee.getEmployeeId();

        if (branch.isCashierBusy(cashierId)) {
            return "ERROR|You are already serving a customer! Please wait.";
        }

        Customer customer;
        synchronized (branch) {
            customer = branch.getNextCustomer();
            if (customer == null) {
                return "ERROR|No customers in queue";
            }
        }

        branch.setCashierBusy(cashierId, true);

        CustomerHandler handler = new CustomerHandler(customer, branch, cashierId);
        branch.submitCustomerForCheckout(handler);

        return "OK|Started serving: (" + customer.getClass().getSimpleName() + ") " + customer.getFullName()
                + "|Customers remaining in queue: " + branch.getQueueSize();
    }

    private String handleViewCustomers() {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in";
        }

        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return "ERROR|Branch not found";
            }

            StringBuilder str = new StringBuilder("OK|Customers in " + branch.getName() + "|");
            for (Customer customer : branch.getCustomerQueue()) {
                str.append(customer.toString()).append("|");
            }

            return str.toString();
        }
    }

    private String handleGetEmployees() {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in";
        }

        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return "ERROR|Branch not found";
            }

            StringBuilder list = new StringBuilder();
            for (Employee emp : branch.getEmployees()) {
                list.append(emp.getEmployeeId())
                        .append(" - ")
                        .append(emp.getFullName())
                        .append(" - ")
                        .append(emp.getRole())
                        .append("\\n");
            }

            return "OK|Employees in " + branch.getName() + "|employees:" + list.toString();
        }
    }

    private String handleSendMessage(HashMap<String, String> data) {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in to chat";
        }

        String message = data.get("message");
        if (message == null || message.isEmpty()) {
            return "ERROR|Empty message";
        }

        return "OK|Message sent";
    }

    private void cleanup() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        server.removeClient(this);
    }

    private String handleGetOtherBranchEmployees() {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in";
        }

        StringBuilder list = new StringBuilder();
        int count = 0;

        for (ClientHandler client : server.getConnectedClients()) {
            if (client == this)
                continue;

            if (!client.isAvailableForChat())
                continue;

            if (client.getCurrentEmployee() == null)
                continue;

            if (!client.getBranchId().equals(this.branchId)) {
                Employee emp = client.getCurrentEmployee();
                list.append(emp.getEmployeeId())
                        .append(" - ")
                        .append(emp.getFullName())
                        .append(" (Branch: ")
                        .append(client.getBranchId())
                        .append(")\\n");
                count++;
            }
        }

        if (count == 0) {
            return "OK|No employees from other branches are connected|employees:";
        }

        return "OK|Found " + count + " employee(s) from other branches|employees:" + list.toString();
    }

    private String handleCheckChatAvailability(HashMap<String, String> data) {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in";
        }

        String targetEmpId = data.get("targetEmployeeId");
        if (targetEmpId == null || targetEmpId.isEmpty()) {
            return "ERROR|Missing target employee ID";
        }

        ClientHandler targetClient = null;
        for (ClientHandler client : server.getConnectedClients()) {
            if (client == this)
                continue;

            if (client.getCurrentEmployee() != null &&
                    client.getCurrentEmployee().getEmployeeId().equals(targetEmpId)) {
                targetClient = client;
                break;
            }
        }

        if (targetClient == null) {
            return "ERROR|Employee " + targetEmpId + " is not connected or not logged in";
        }

        if (targetClient.getBranchId().equals(this.branchId)) {
            return "ERROR|Cannot start chat with employees from the same branch";
        }

        synchronized (chain) {
            StoreBranch targetBranch = chain.getBranchById(targetClient.getBranchId());
            if (targetBranch != null && targetBranch.isCashierBusy(targetEmpId)) {
                return "ERROR|Employee " + targetEmpId + " is currently busy with a customer. Please try again later.";
            }
        }

        return "OK|Employee " + targetEmpId + " is available for chat";
    }

    private String handleStartChat(HashMap<String, String> data) {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in";
        }

        String targetEmpId = data.get("targetEmployeeId");
        if (targetEmpId == null || targetEmpId.isEmpty()) {
            return "ERROR|Missing target employee ID";
        }

        ClientHandler targetClient = null;
        for (ClientHandler client : server.getConnectedClients()) {
            if (client == this)
                continue;

            if (client.getCurrentEmployee() != null &&
                    client.getCurrentEmployee().getEmployeeId().equals(targetEmpId)) {
                targetClient = client;
                break;
            }
        }

        if (targetClient == null) {
            ChatAvailabilityObserver.subscribe(currentEmployee.getEmployeeId(), targetEmpId);
            return "PENDING|Employee " + targetEmpId
                    + " is not connected. You will be notified when they are available.";
        }

        if (targetClient.getBranchId().equals(this.branchId)) {
            return "ERROR|Cannot start chat with employees from the same branch";
        }

        synchronized (chain) {
            StoreBranch targetBranch = chain.getBranchById(targetClient.getBranchId());
            if (targetBranch != null && targetBranch.isCashierBusy(targetEmpId)) {
                ChatAvailabilityObserver.subscribe(currentEmployee.getEmployeeId(), targetEmpId);
                return "PENDING|Employee " + targetEmpId
                        + " is currently busy with a customer. You will be notified when they are available.";
            }
        }

        if (targetClient.getChatPartnerId() != null) {
            ChatAvailabilityObserver.subscribe(currentEmployee.getEmployeeId(), targetEmpId);
            return "PENDING|Employee " + targetEmpId
                    + " is currently in another chat. You will be notified when they are available.";
        }

        this.chatPartnerId = targetEmpId;
        targetClient.setChatPartnerId(currentEmployee.getEmployeeId());

        targetClient.sendChatRequest(currentEmployee.getEmployeeId(), currentEmployee.getFullName());

        Logger.write("Chat started: " + currentEmployee.getEmployeeId() + " -> " + targetEmpId);

        return "OK|Chat session started with " + targetEmpId;
    }

    private String handleAcceptChat(HashMap<String, String> data) {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in";
        }
        return "OK|Chat accepted";
    }

    private void notifyWaitingEmployees() {
        if (currentEmployee == null) {
            return;
        }

        String myEmpId = currentEmployee.getEmployeeId();
        List<String> waitingEmployees = ChatAvailabilityObserver.getAndClearWaitingRequesters(myEmpId);

        for (String requesterEmpId : waitingEmployees) {
            for (ClientHandler client : server.getConnectedClients()) {
                if (client.getCurrentEmployee() != null &&
                        client.getCurrentEmployee().getEmployeeId().equals(requesterEmpId)) {
                    client.sendAvailabilityNotification(myEmpId);
                    break;
                }
            }
        }
    }

    private String handleEndChat(HashMap<String, String> data) {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in";
        }

        String targetEmpId = data.get("targetEmployeeId");
        if (targetEmpId == null || targetEmpId.isEmpty()) {
            return "ERROR|Missing target employee ID";
        }

        this.chatPartnerId = null;

        for (ClientHandler client : server.getConnectedClients()) {
            if (client.getCurrentEmployee() != null &&
                    client.getCurrentEmployee().getEmployeeId().equals(targetEmpId)) {
                client.setChatPartnerId(null);
                client.sendChatEnd(currentEmployee.getEmployeeId());
                break;
            }
        }

        String chatKey = getChatKey(currentEmployee.getEmployeeId(), targetEmpId);
        List<String> observers;
        synchronized (chatObservers) {
            observers = chatObservers.remove(chatKey);
        }
        if (observers != null) {
            for (String observerId : observers) {
                for (ClientHandler client : server.getConnectedClients()) {
                    if (client.getCurrentEmployee() != null &&
                            client.getCurrentEmployee().getEmployeeId().equals(observerId)) {
                        client.sendChatEnd(currentEmployee.getEmployeeId());
                        break;
                    }
                }
            }
        }

        Logger.write("Chat ended by: " + currentEmployee.getEmployeeId());

        notifyWaitingEmployees();

        return "OK|Chat ended";
    }

    private String handleSendChatMessage(HashMap<String, String> data) {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in";
        }

        String targetEmpId = data.get("targetEmployeeId");
        String message = data.get("message");

        if (targetEmpId == null || message == null) {
            return "ERROR|Missing target employee ID or message";
        }

        ClientHandler targetClient = null;
        for (ClientHandler client : server.getConnectedClients()) {
            if (client.getCurrentEmployee() != null &&
                    client.getCurrentEmployee().getEmployeeId().equals(targetEmpId)) {
                targetClient = client;
                break;
            }
        }

        if (targetClient == null) {
            return "ERROR|Employee " + targetEmpId + " is no longer connected";
        }

        targetClient.sendChatMessage(currentEmployee.getEmployeeId(), message);

        String chatKey = getChatKey(currentEmployee.getEmployeeId(), targetEmpId);
        List<String> observers = chatObservers.get(chatKey);
        if (observers != null) {
            for (String observerId : observers) {
                for (ClientHandler client : server.getConnectedClients()) {
                    if (client.getCurrentEmployee() != null &&
                            client.getCurrentEmployee().getEmployeeId().equals(observerId)) {
                        client.sendChatMessage(currentEmployee.getEmployeeId(), message);
                        break;
                    }
                }
            }
        }

        Logger.write("Chat: " + currentEmployee.getEmployeeId() + " -> " + targetEmpId + ": " + message);

        return "OK|Message sent";
    }

    public void sendChatRequest(String fromEmployeeId, String fromName) {
        if (out != null) {
            out.println("CHAT_REQUEST|" + fromEmployeeId + "|" + fromName);
            out.flush();
        }
    }

    public void sendChatMessage(String fromEmployeeId, String message) {
        if (out != null) {
            out.println("CHAT_MESSAGE|" + fromEmployeeId + "|" + message);
            out.flush();
        }
    }

    public void sendChatEnd(String byEmployeeId) {
        if (out != null) {
            out.println("CHAT_END|" + byEmployeeId);
            out.flush();
        }
    }

    public void sendAvailabilityNotification(String availableEmployeeId) {
        if (out != null) {
            out.println("CHAT_AVAILABLE|" + availableEmployeeId);
            out.flush();
        }
    }

    public String getBranchId() {
        return branchId;
    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public String getChatPartnerId() {
        return chatPartnerId;
    }

    public void setChatPartnerId(String partnerId) {
        this.chatPartnerId = partnerId;
    }

    private String handleGetActiveChats() {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in";
        }

        if (currentEmployee.getRole() != Role.MANAGER) {
            return "ERROR|Only managers can monitor chats";
        }

        StringBuilder list = new StringBuilder();
        int count = 0;
        java.util.Set<String> seenChatPairs = new java.util.HashSet<>();

        for (ClientHandler client : server.getConnectedClients()) {
            if (client == this)
                continue;

            if (client.getCurrentEmployee() != null && client.getChatPartnerId() != null) {
                Employee emp = client.getCurrentEmployee();
                String empId = emp.getEmployeeId();
                String partnerId = client.getChatPartnerId();

                String pairKey = empId.compareTo(partnerId) < 0
                        ? empId + "|" + partnerId
                        : partnerId + "|" + empId;

                if (seenChatPairs.contains(pairKey)) {
                    continue;
                }
                seenChatPairs.add(pairKey);

                list.append(empId + " <-> " + partnerId + "\\n");
                count++;
            }
        }

        if (count == 0) {
            return "OK|No active chat sessions|chats:";
        }

        return "OK|Found " + count + " active chat session(s)|chats:" + list.toString();
    }

    private String handleJoinChat(HashMap<String, String> data) {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in";
        }

        if (currentEmployee.getRole() != Role.MANAGER) {
            return "ERROR|Only managers can join chats";
        }

        String targetEmpId = data.get("targetEmployeeId");
        if (targetEmpId == null || targetEmpId.isEmpty()) {
            return "ERROR|Missing target employee ID";
        }

        ClientHandler targetClient = null;
        for (ClientHandler client : server.getConnectedClients()) {
            if (client.getCurrentEmployee() != null &&
                    client.getCurrentEmployee().getEmployeeId().equals(targetEmpId)) {
                targetClient = client;
                break;
            }
        }

        if (targetClient == null) {
            return "ERROR|Employee " + targetEmpId + " is not connected";
        }

        String partnerId = targetClient.getChatPartnerId();
        if (partnerId == null) {
            return "ERROR|Employee " + targetEmpId + " is not in an active chat";
        }

        String managerName = currentEmployee.getFullName();
        String managerId = currentEmployee.getEmployeeId();
        targetClient.sendChatMessage("SYSTEM",
                "Manager '" + managerName + "'' (" + managerId + ") has joined the chat");

        for (ClientHandler client : server.getConnectedClients()) {
            if (client.getCurrentEmployee() != null &&
                    client.getCurrentEmployee().getEmployeeId().equals(partnerId)) {
                client.sendChatMessage("SYSTEM",
                        "Manager '" + managerName + "'' (" + managerId + ") has joined the chat");
                break;
            }
        }

        String chatKey = getChatKey(targetEmpId, partnerId);
        synchronized (chatObservers) {
            chatObservers.computeIfAbsent(chatKey, k -> new java.util.ArrayList<>()).add(managerId);
        }

        Logger.write("Manager " + managerId + " joined chat between " + targetEmpId + " and " + partnerId);

        return "OK|Joined chat session|chatPartnerId:" + partnerId;
    }

    private String handleLeaveChat(HashMap<String, String> data) {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in";
        }

        String targetEmpId = data.get("targetEmployeeId");
        String chatPartner = data.get("chatPartnerId");
        String managerId = currentEmployee.getEmployeeId();
        String managerName = currentEmployee.getFullName();

        if (targetEmpId != null && chatPartner != null) {
            String chatKey = getChatKey(targetEmpId, chatPartner);
            synchronized (chatObservers) {
                List<String> observers = chatObservers.get(chatKey);
                if (observers != null) {
                    observers.remove(managerId);
                    if (observers.isEmpty()) {
                        chatObservers.remove(chatKey);
                    }
                }
            }

            for (ClientHandler client : server.getConnectedClients()) {
                if (client.getCurrentEmployee() != null) {
                    String empId = client.getCurrentEmployee().getEmployeeId();
                    if (empId.equals(targetEmpId) || empId.equals(chatPartner)) {
                        client.sendChatMessage("SYSTEM",
                                "Manager '" + managerName + "'' (" + managerId + ") has left the chat");
                    }
                }
            }
        }

        Logger.write("Manager " + managerId + " left chat monitoring");

        return "OK|Left chat session";
    }

    public boolean isAvailableForChat() {
        return isAvailableForChat;
    }

    private String handleAdminChatMessage(HashMap<String, String> data) {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in";
        }

        if (currentEmployee.getRole() != Role.MANAGER) {
            return "ERROR|Only managers can use this command";
        }

        String targetEmpId = data.get("targetEmployeeId");
        String chatPartnerId = data.get("chatPartnerId");
        String message = data.get("message");

        if (targetEmpId == null || chatPartnerId == null || message == null) {
            return "ERROR|Missing required parameters";
        }

        String adminId = currentEmployee.getEmployeeId();

        for (ClientHandler client : server.getConnectedClients()) {
            if (client.getCurrentEmployee() != null) {
                String empId = client.getCurrentEmployee().getEmployeeId();
                if (empId.equals(targetEmpId) || empId.equals(chatPartnerId)) {
                    client.sendChatMessage(adminId + " (Manager)", message);
                }
            }
        }

        Logger.write("Admin chat: " + adminId + " -> [" + targetEmpId + ", " + chatPartnerId + "]: " + message);

        return "OK|Message sent to both participants";
    }
}
