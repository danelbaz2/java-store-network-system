package com.storemgmt.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

import com.storemgmt.network.Message;
import com.storemgmt.network.MessageType;
import com.storemgmt.chat.ChatRequest;
import com.storemgmt.model.customer.Customer;
import com.storemgmt.model.employee.Employee;
import com.storemgmt.model.employee.Role;
import com.storemgmt.model.product.Product;
import com.storemgmt.model.store.Chain;
import com.storemgmt.model.store.StoreBranch;
import com.storemgmt.utils.JsonLoader;
import com.storemgmt.utils.Logger;
import com.storemgmt.chat.ChatManager;
import com.storemgmt.chat.ChatObserver;
import com.storemgmt.chat.ChatRequest;

public class ClientHandler implements Runnable, ChatObserver {

    private Socket socket;
    private Chain chain;
    private StoreServer server;

    private DataInputStream in;
    private PrintStream out;

    private String branchId;
    private Employee currentEmployee;

    private static final Map<String, ClientHandler> connectedClients = new HashMap<>();
    private static final Map<String, String> activeChats = new HashMap<>();

    private static final Map<String, Set<String>> chatRooms = new HashMap<>();
    

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

                // Parse: ACTION|branchId|key1:value1|key2:value2
                String[] parts = line.split("\\|");
                String action = parts[0];
                branchId = parts[1];

                // Get data into map
                HashMap<String, String> data = new HashMap<>();
                for (int i = 2; i < parts.length; i++) {
                    String[] kv = parts[i].split(":", 2);
                    if (kv.length == 2) {
                        data.put(kv[0], kv[1]);
                    }
                }

                // Handle the action
                String response = handleAction(action, data);
                out.println(response);
                out.flush();
            }

        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            ChatManager.getInstance().removeObserver(this);
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
            case "SELL_PRODUCT":
                return handleSellProduct();
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
                return handleShowReportByBranch();
            case "SHOW_REPORT_BY_CATEGORY":
                return handleShowReportByCategory();
            case "SHOW_REPORT_BY_PRODUCT":
                return handleShowReportByProduct();

            // new 10.01.2026

            case "OPEN_CHAT":
                return handleOpenChat(data);
            case "REQUEST_CHAT":
                return handleRequestChat();

            // new 12.01.2026

            case "END_CHAT":
                return handleEndChat(data);

            // new 13.01.2026

            case "GET_ACTIVE_CHATS":
                return handleGetActiveChats();
            case "JOIN_CHAT":
                return handleJoinChat(data);

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
            JsonLoader.set("products." + productId + ".stock", Integer.parseInt(quantity));
            JsonLoader.save();
            return "OK|Inventory updated successfully";
        }
    }

    // Active chats to the manager
    private String handleGetActiveChats() {
        if (chatRooms.isEmpty()) {
            return "ERROR|No active chats right now.";
        }
        
        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (String key : chatRooms.keySet()) {
            sb.append(count++).append(". Chat Room: ").append(key)
              .append(" (Participants: ").append(chatRooms.get(key)).append(")\\n");
        }
        return "OK|Active Chats list:|list:" + sb.toString();
    }

    // Manager join the conversation
    private String handleJoinChat(HashMap<String, String> data) {
        String managerId = data.get("managerId");
        String targetId = data.get("targetId");

        String chatKey = activeChats.get(targetId);
        if (chatKey == null) {
            return "ERROR|Employee " + targetId + " is not currently in a chat.";
        }

        Set<String> participants = chatRooms.get(chatKey);
        if (participants == null) return "ERROR|Room not found";
        
        participants.add(managerId);
        activeChats.put(managerId, chatKey); 

        for (String pid : participants) {
            
            if (pid.equals(managerId)) continue; 

            ClientHandler handler = connectedClients.get(pid);
            if (handler != null) {
                handler.out.println("CHAT|System: MANAGER " + managerId + " has joined the chat.");
                handler.out.flush();
            }
        }

        return "OK|Joined chat successfully.";
    }

    private String handleShowReportByBranch() {
        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return "ERROR|Branch not found: " + branchId;
            }
            return "OK|" + branch.getReportByBranch();
        }
    }

    private String handleShowReportByCategory() {
        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return "ERROR|Branch not found: " + branchId;
            }
            return "OK|" + branch.getReportByCategory();
        }
    }

    private String handleShowReportByProduct() {
        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return "ERROR|Branch not found: " + branchId;
            }
            return "OK|" + branch.getReportByProduct();
        }
    }

    private String handleEndChat(HashMap<String, String> data) {
        String fromId = data.get("from");
        String chatKey = activeChats.get(fromId);

        if (chatKey == null) {
            return "OK|Chat already closed";
        }

        Set<String> participants = chatRooms.get(chatKey);

        String[] parts = chatKey.split("_");
        String targetId = parts[0].equals(fromId) ? parts[1] : parts[0];

        if (participants != null) {
            for (String pid : participants) {
                activeChats.remove(pid);
                if (pid.equals(fromId)) continue;

                ClientHandler handler = connectedClients.get(pid);
                if (handler != null) {
                    handler.out.println("CHAT_ENDED|System: The chat was ended by " + fromId);
                    handler.out.flush();
                }
            }
            chatRooms.remove(chatKey);
        }
        
        // ClientHandler targetHandler = connectedClients.get(targetId);
        // if (targetHandler != null) {
        //     targetHandler.out.println("CHAT_ENDED|System: The chat was ended by " + fromId);
        //     targetHandler.out.flush();
        // }

        // activeChats.remove(fromId);
        // activeChats.remove(targetId);

        System.out.println("[Server Log] Chat ended: " + chatKey);
        return "OK|Chat ended. Connection closed.";
    }

    private String handleAddEmployee(HashMap<String, String> data) {

        String empId = data.get("employeeId");
        String fullName = data.get("fullName");
        String phone = data.get("phone");
        String branchId = data.get("branchId");
        String role = data.get("role");
        String password = data.get("password");
        // ?
        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return "ERROR|Branch not found: " + branchId;
            }

            Employee emp = new Employee(empId, fullName, phone, branchId, Role.valueOf(role), password);
            branch.addEmployee(emp);
            Logger.write("Employee added: " + empId);
            return "OK|Employee added successfully";
        }
    }

    private String handleRemoveEmployee(HashMap<String, String> data) {
        String empId = data.get("employeeId");
        String branchId = JsonLoader.getString("employees." + empId + ".branchId");

        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return "ERROR|Branch not found: " + branchId;
            }

            Employee emp = branch.getEmployeeById(empId);
            if (emp == null) {
                return "ERROR|Employee not found: " + empId;
            }

            branch.removeEmployee(emp);
            Logger.write("Employee removed: " + empId);
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

                        // register this employee as connected
                        connectedClients.put(emp.getEmployeeId(), this);

                        // Register for queue updates
                        ChatManager.getInstance().registerObserver(this);

                        return "OK|Login successful" +
                                "|employeeName:" + emp.getFullName() +
                                "|role:" + emp.getRole();
                    } else {
                        return "ERROR|Invalid password";
                    }
                }
            }
            return "ERROR|Employee not found: " + empId;
        }
    }

    private String handleLogout() {
        if (currentEmployee == null) {
            return "ERROR|Not logged in";
        }

        ChatManager.getInstance().removeObserver(this);

        connectedClients.remove(currentEmployee.getEmployeeId());
        activeChats.remove(currentEmployee.getEmployeeId());

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
                if (qty > 0) {
                    inventory.append(p.getProductId())
                            .append(" - ").append(p.getBrand())
                            .append(" ")
                            .append(p.getClass().getSimpleName())
                            .append(" (").append(qty)
                            .append(" units) - $")
                            .append(p.getPrice())
                            .append("\n");
                }
            }

            return "OK|Inventory for " + branch.getName() + "|inventory:" + inventory.toString();
        }
    }

    private String handleSellProduct() {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in to sell";
        }

        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return "ERROR|Branch not found";
            }

            // Get next customer from queue
            Customer customer = branch.getNextCustomer();
            if (customer == null) {
                return "ERROR|No customers in queue";
            }
            Logger.write("Customer served: " + customer.getFullName());

            // Process the sale
            double price = branch.sell(customer);

            return "OK|Served: (" + customer.getClass().getSimpleName() + ") " + customer.getFullName() +
                    "|Remaining Customers in queue: " + branch.getQueueSize() +
                    "|Total Sale: " + String.format("%.2f", price) + "$";
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

        private String handleRequestChat() {

        String currentId = currentEmployee.getEmployeeId(); 
        String targetId = null;
        
        if (currentEmployee == null) return "ERROR|Must be logged in";

        // Searching for employee
        for (String empId : connectedClients.keySet()) {
            if (!empId.equals(currentId) && !activeChats.containsKey(empId)) {
                targetId = empId;
                break; 
            }
        }

        if (targetId != null) {
            // Open Chat
            HashMap<String, String> openData = new HashMap<>();

            openData.put("from", currentEmployee.getEmployeeId()); 
            openData.put("to", targetId);
            String msg = "User " + currentEmployee.getFullName() + " (" + currentId + ") requested a chat with you.";
            openData.put("message", msg);
            return handleOpenChat(openData);
        }

        else {
            // Add chat to the QUEUE
            ChatManager.getInstance().addRequest(currentEmployee.getEmployeeId());
            return "OK|All representatives are busy. You have been added to the waiting list.";
        }
    }


    private String handleSendMessage(HashMap<String, String> data) {

        String fromId = data.get("from");
        String message = data.get("message");

        String chatKey = activeChats.get(fromId);
        if (chatKey == null) {
            return "ERROR|No active chat";
        }

        Set<String> participants = chatRooms.get(chatKey);
        if (participants == null) return "ERROR|Chat room corrupted";

        // Send message to all the participants except me 
        for (String participantId : participants) {
            if (participantId.equals(fromId)) continue; // לא לשלוח לעצמי

            ClientHandler handler = connectedClients.get(participantId);
            if (handler != null) {
                handler.out.println("CHAT|" + fromId + ": " + message);
                handler.out.flush();
            }
        }

        // String[] parts = chatKey.split("_");
        // String targetId = parts[0].equals(fromId) ? parts[1] : parts[0];

        // ClientHandler targetHandler = connectedClients.get(targetId);
        // if (targetHandler == null) {
        //     return "ERROR|Chat partner disconnected";
        // }

        // targetHandler.out.println(
        //     "CHAT|" + fromId + ": " + message
        // );
        // targetHandler.out.flush();

        return "OK|Message sent";
    }

    private String handleOpenChat(HashMap<String, String> data) {

        if (currentEmployee == null) {
            return "ERROR|Must be logged in to open chat";
        }

        String fromId = data.get("from");
        String toId = data.get("to");
        String message = data.get("message");

        if (fromId == null || toId == null || message == null) {
            return "ERROR|Missing chat data";
        }

        ClientHandler targetHandler = connectedClients.get(toId);
        if (targetHandler == null) {
            return "ERROR|Target employee not connected";
        }

        String chatKey = fromId + "_" + toId;
        activeChats.put(fromId, chatKey);
        activeChats.put(toId, chatKey);

        // Create Chat Rooms
        Set<String> participants = new HashSet<>();
        participants.add(fromId);
        participants.add(toId);
        chatRooms.put(chatKey, participants);

        targetHandler.out.println("CHAT|System: Chat started with " + fromId);
        targetHandler.out.println("CHAT|" + message);
        targetHandler.out.flush();

        return "OK|Chat started with employee " + toId;
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

    public String getBranchId() {
        return branchId;
    }

    @Override
    public void onQueueUpdate(ChatRequest nextRequest) {
        
        if (currentEmployee != null && 
            !activeChats.containsKey(currentEmployee.getEmployeeId()) && 
            !nextRequest.getRequesterId().equals(currentEmployee.getEmployeeId())) {

            out.println("QUEUE_UPDATE|" + nextRequest.getRequesterId());
            out.flush();
        }
    }
}
