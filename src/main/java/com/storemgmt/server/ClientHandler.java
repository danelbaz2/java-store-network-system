package com.storemgmt.server;

import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.storemgmt.model.customer.Customer;
import com.storemgmt.model.employee.Employee;
import com.storemgmt.model.employee.Role;
import com.storemgmt.model.product.Product;
import com.storemgmt.model.store.Chain;
import com.storemgmt.model.store.StoreBranch;
import com.storemgmt.utils.JsonLoader;
import com.storemgmt.utils.Logger;

public class ClientHandler implements Runnable {

    private Socket socket;
    private Chain chain;
    private StoreServer server;

    private DataInputStream in;
    private PrintStream out;

    private String branchId;
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
                return handleShowReport("branch");
            case "SHOW_REPORT_BY_CATEGORY":
                return handleShowReport("category");
            case "SHOW_REPORT_BY_PRODUCT":
                return handleShowReport("product");
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

    private String handleShowReport(String type) {
        try {
            synchronized (chain) {
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

                    returnString.append(branchReport);

                    // Also write a nice version to Word
                    wordWriter.write("Branch: " + branch.getName() + "\n");
                    wordWriter.write("----------------------------------------\n");
                    // Parse the pipe-separated report for Word
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

        // check if the employee is already logged in
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
            return "ERROR|Employee not found: " + empId;
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
                if (qty > 0) {
                    inventory.append(p.getProductId())
                            .append(" - ").append(p.getBrand())
                            .append(" ")
                            .append(p.getClass().getSimpleName())
                            .append(" (").append(qty)
                            .append(" units) - $")
                            .append(p.getPrice())
                            .append("\\n"); // Escaped newline - will be unescaped on client
                }
            }

            return "OK|Inventory for " + branch.getName() + "|" + inventory.toString();
        }
    }

    private String handleSellProduct() {
        if (currentEmployee == null) {
            return "ERROR|Must be logged in to sell";
        }

        StoreBranch branch = chain.getBranchById(branchId);
        if (branch == null) {
            return "ERROR|Branch not found";
        }

        String cashierId = currentEmployee.getEmployeeId();

        // Check if this cashier is already serving a customer
        if (branch.isCashierBusy(cashierId)) {
            return "ERROR|You are already serving a customer! Please wait.";
        }

        // Get next customer from queue (synchronized access)
        Customer customer;
        synchronized (branch) {
            customer = branch.getNextCustomer();
            if (customer == null) {
                return "ERROR|No customers in queue";
            }
        }

        // Mark this cashier as busy BEFORE submitting to pool
        branch.setCashierBusy(cashierId, true);

        CustomerHandler handler = new CustomerHandler(customer, branch, cashierId);
        branch.submitCustomerForCheckout(handler);

        return "OK|Started serving: (" + customer.getClass().getSimpleName() + ") " + customer.getFullName()
                + "|Customers remaining in queue: " + branch.getQueueSize();
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
