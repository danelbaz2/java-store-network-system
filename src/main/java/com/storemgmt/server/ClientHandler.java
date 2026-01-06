package com.storemgmt.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.storemgmt.model.employee.Employee;
import com.storemgmt.model.employee.Role;
import com.storemgmt.model.product.Product;
import com.storemgmt.model.store.Chain;
import com.storemgmt.model.store.StoreBranch;
import com.storemgmt.security.PasswordValidator;

public class ClientHandler implements Runnable {

    private Socket socket;
    private Chain chain;
    private StoreServer server;

    private DataInputStream in;
    private PrintStream out;

    private String branchId;
    private String sessionId;
    private Employee currentEmployee;

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
            case "SELL_PRODUCT":
                return handleSellProduct(data);
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

            branch.updateProductQuantity(product, Integer.parseInt(quantity));
            return "OK|Inventory updated successfully";
        }
    }

    private String handleAddEmployee(HashMap<String, String> data) {

        String empId = data.get("employeeId");
        String fullName = data.get("fullName");
        String phone = data.get("phone");
        String branchId = data.get("branchId");
        String role = data.get("role");
        String password = data.get("password");

        if (fieldIsEmpty(empId, fullName, phone, branchId, role, password)) {
            return "ERROR|Missing employeeId, fullName, phone, branchId, role, or password";
        }

        if (!PasswordValidator.isValid(password)) {
            return "ERROR|Invalid password";
        }

        // ?
        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return "ERROR|Branch not found: " + branchId;
            }

            Employee emp = new Employee(empId, fullName, phone, branchId, Role.valueOf(role), password);
            branch.addEmployee(emp);
            return "OK|Employee added successfully";
        }
    }

    private String handleRemoveEmployee(HashMap<String, String> data) {
        String empId = data.get("employeeId");
        String branchId = data.get("branchId");

        if (empId.isEmpty() || branchId.isEmpty()) {
            return "ERROR|Missing employeeId or branchId";
        }

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
            return "OK|Employee removed successfully";
        }
    }

    private boolean fieldIsEmpty(String empId, String fullName, String phone, String branchId2, String role,
            String password) {
        return empId.isEmpty() || fullName.isEmpty() || phone.isEmpty() || branchId2.isEmpty() || role.isEmpty()
                || password.isEmpty();
    }

    private String handleLogin(HashMap<String, String> data) {
        String empId = data.get("employeeId");
        String password = data.get("password");

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
                        sessionId = "SESSION-" + System.currentTimeMillis();

                        return "OK|Login successful|sessionId:" + sessionId +
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

        String name = currentEmployee.getFullName();
        currentEmployee = null;
        sessionId = null;
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
                            .append("\\n");
                }
            }

            return "OK|Inventory for " + branch.getName() + "|inventory:" + inventory.toString();
        }
    }

    private String handleSellProduct(HashMap<String, String> data) {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in to sell";
        }

        String type = data.get("productType");
        String brand = data.get("brand");
        String qty = data.get("quantity");

        if (type == null || brand == null || qty == null) {
            return "ERROR|Missing product details";
        }

        return "OK|Sale recorded: " + type + " - " + brand + " x" + qty;
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

    public String getBranchId() {
        return branchId;
    }
}
