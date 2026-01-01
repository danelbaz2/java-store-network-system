package com.storemgmt.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;

import com.storemgmt.model.product.Product;

import com.storemgmt.model.employee.Employee;
import com.storemgmt.model.store.Chain;
import com.storemgmt.model.store.StoreBranch;
import com.storemgmt.protocol.ActionType;
import com.storemgmt.protocol.Request;
import com.storemgmt.protocol.Response;

/**
 * Handles communication with one client in its own thread.
 * 
 * How it works:
 * 1. Receives Request objects from the client
 * 2. Routes each request to the appropriate handler method
 * 3. Sends Response objects back to the client
 * 4. Uses synchronized access to shared Chain data
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final Chain chain;
    private final StoreServer server;

    private DataInputStream inputStream;
    private PrintStream outputStream;

    private String branchId; // Which branch this client represents
    private String sessionId; // Current user's session (null if not logged in)
    private Employee currentEmployee; // Currently logged in employee

    public ClientHandler(Socket socket, Chain chain, StoreServer server) {
        this.socket = socket;
        this.chain = chain;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            // Create streams for communication (output first to avoid deadlock)
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new PrintStream(socket.getOutputStream());

            System.out.println("Client handler ready for communication");

            // Main loop - receive and process requests
            while (true) {
                try {
                    // Wait for a request from client
                    Request request = Request.cast(inputStream.readLine());

                    // Store the branch ID from the request
                    if (request.getBranchId() != null) {
                        this.branchId = request.getBranchId();
                    }

                    System.out.println("Received: " + request);

                    // Route to appropriate handler and get response
                    Response response = handleRequest(request);

                    // Send response back to client
                    outputStream.println(response.toString());
                    outputStream.flush();

                } catch (Exception e) {
                    System.err.println("Unknown object received: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    // Route request to appropriate handler based on action type
    private Response handleRequest(Request request) {
        ActionType action = request.getAction();

        switch (action) {
            case LOGIN:
                return handleLogin(request);
            case LOGOUT:
                return handleLogout(request);
            case GET_INVENTORY:
                return handleGetInventory(request);
            case SELL_PRODUCT:
                return handleSellProduct(request);
            case GET_EMPLOYEES:
                return handleGetEmployees(request);
            case SEND_MESSAGE:
                return handleSendMessage(request);
            default:
                return Response.error("Unknown action: " + action);
        }
    }

    // Handle login request
    private Response handleLogin(Request request) {
        String employeeId = request.get("employeeId");
        String password = request.get("password");

        if (employeeId == null || password == null) {
            return Response.error("Missing employeeId or password");
        }

        // Find employee in the branch
        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return Response.error("Branch not found: " + branchId);
            }

            // Search for employee
            for (Employee emp : branch.getEmployees()) {
                if (emp.getEmployeeId().equals(employeeId)) {
                    // Check password
                    if (emp.getPassword().equals(password)) {
                        this.currentEmployee = emp;
                        this.sessionId = "SESSION-" + System.currentTimeMillis();

                        return Response.success("Login successful")
                                .put("sessionId", sessionId)
                                .put("employeeName", emp.getFullName())
                                .put("role", emp.getRole().toString());
                    } else {
                        return Response.error("Invalid password");
                    }
                }
            }

            return Response.error("Employee not found: " + employeeId);
        }
    }

    // Handle logout request
    private Response handleLogout(Request request) {
        if (currentEmployee == null) {
            return Response.error("Not logged in");
        }

        String name = currentEmployee.getFullName();
        currentEmployee = null;
        sessionId = null;

        return Response.success("Goodbye, " + name);
    }

    // Handle get inventory request
    private Response handleGetInventory(Request request) {
        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return Response.error("Branch not found: " + branchId);
            }

            // Build inventory string
            StringBuilder inventory = new StringBuilder();
            for (Map.Entry<Product, Integer> entry : branch.getProducts().entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                if (quantity > 0) {
                    inventory.append(product.toString()).append(" - Qty: ").append(quantity).append("\n");
                }
            }

            return Response.success("Inventory for " + branch.getName()).put("inventory", inventory.toString())
                    .put("itemCount", branch.getProducts().size());
        }
    }

    // Handle sell product request (synchronized for thread safety)
    private Response handleSellProduct(Request request) {
        if (currentEmployee == null) {
            return Response.error("Must be logged in to sell");
        }

        String productType = request.get("productType");
        String brand = request.get("brand");
        String quantityStr = request.get("quantity");

        if (productType == null || brand == null || quantityStr == null) {
            return Response.error("Missing product details");
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            return Response.error("Invalid quantity");
        }

        return Response.success("Sale recorded: " + productType + " - " + brand + " - " + quantity);
    }

    // Handle get employees request (admin only)
    private Response handleGetEmployees(Request request) {
        if (currentEmployee == null) {
            return Response.error("Must be logged in");
        }

        synchronized (chain) {
            StoreBranch branch = chain.getBranchById(branchId);
            if (branch == null) {
                return Response.error("Branch not found");
            }

            StringBuilder list = new StringBuilder();
            for (Employee emp : branch.getEmployees()) {
                list.append(emp.getEmployeeId()).append(" - ").append(emp.getFullName()).append(" (")
                        .append(emp.getRole()).append(")\n");
            }

            return Response.success("Employees in " + branch.getName()).put("employees", list.toString());
        }
    }

    // Handle chat message
    private Response handleSendMessage(Request request) {
        if (currentEmployee == null) {
            return Response.error("Must be logged in to chat");
        }

        String message = request.get("message");
        if (message == null || message.isEmpty()) {
            return Response.error("Empty message");
        }

        // TODO: Broadcast to other clients

        return Response.success("Message sent (TODO: implement broadcast)");
    }

    // Send a message to this client (for chat broadcasting)
    public void sendMessage(Response response) {
        try {
            outputStream.println(response.toString());
            outputStream.flush();
        } catch (Exception e) {
            System.err.println("Error sending message to client: " + e.getMessage());
        }
    }

    // Cleanup when client disconnects
    private void cleanup() {
        try {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }

        server.removeClient(this);
    }

    public String getBranchId() {
        return branchId;
    }
}
