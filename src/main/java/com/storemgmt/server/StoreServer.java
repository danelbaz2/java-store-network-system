package com.storemgmt.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import com.storemgmt.model.store.Chain;

/**
 * TCP Server that listens for client connections.
 * 
 * How it works:
 * 1. Server starts and listens on a port (default 5000)
 * 2. When a client connects, server creates a new ClientHandler thread
 * 3. Each ClientHandler manages one client connection
 * 4. All handlers share the same Chain instance (synchronized for thread
 * safety)
 */
public class StoreServer {

    private final Chain chain; // Shared data - all branches and customers
    private final int port; // Port to listen on
    private ServerSocket serverSocket; // The server socket
    private boolean running; // Flag to control server loop

    // List of all connected clients (thread-safe list)
    private final CopyOnWriteArrayList<ClientHandler> connectedClients;

    public StoreServer(Chain chain, int port) {
        this.chain = chain;
        this.port = port;
        this.connectedClients = new CopyOnWriteArrayList<>();
    }

    // Start the server and begin accepting connections
    public void start() {
        running = true;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("===========================================");
            System.out.println(" Store Server started on port " + port);
            System.out.println("===========================================");
            System.out.println("Waiting for client connections...\n");

            // Main server loop - accept connections forever
            while (running) {
                try {
                    // Wait for a client to connect (blocks until connection)
                    Socket clientSocket = serverSocket.accept();

                    System.out.println("New client connected from: " + clientSocket.getInetAddress());

                    // Create a handler for this client
                    ClientHandler handler = new ClientHandler(clientSocket, chain, this);
                    connectedClients.add(handler);

                    // Start the handler in a new thread
                    Thread handlerThread = new Thread(handler);
                    handlerThread.start();

                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server on port " + port + ": " + e.getMessage());
        }
    }

    // Stop the server
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
    }

    // Remove a client handler when it disconnects
    public void removeClient(ClientHandler handler) {
        connectedClients.remove(handler);
        System.out.println("Client disconnected. Active clients: " + connectedClients.size());
    }

    // Get all connected clients (for chat broadcasting)
    public CopyOnWriteArrayList<ClientHandler> getConnectedClients() {
        return connectedClients;
    }

    // Get the chain instance
    public Chain getChain() {
        return chain;
    }
}
