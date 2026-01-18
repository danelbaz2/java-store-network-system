package com.storemgmt.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.storemgmt.model.store.Chain;
import com.storemgmt.model.store.StoreBranch;

public class StoreServer {

    private final Chain chain;
    private final int port;
    private ServerSocket serverSocket;
    private boolean running;

    private final ArrayList<ClientHandler> connectedClients;

    public StoreServer(Chain chain, int port) {
        this.chain = chain;
        this.port = port;
        this.connectedClients = new ArrayList<>();
    }

    public void start() {
        running = true;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("===========================================");
            System.out.println(" Store Server started on port " + port);
            System.out.println("===========================================");
            System.out.println("Waiting for client connections...\n");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    System.out.println("New client connected from: " + clientSocket.getInetAddress());

                    ClientHandler handler = new ClientHandler(clientSocket, chain, this);
                    connectedClients.add(handler);

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

    public void stop() {
        running = false;
        try {
            // Shutdown all branch thread pools
            for (StoreBranch branch : chain.getBranches()) {
                branch.shutdown();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        } finally {
            System.out.println("Bye bye");
        }
    }

    public void removeClient(ClientHandler handler) {
        connectedClients.remove(handler);
        System.out.println("Client disconnected");
    }

    public ArrayList<ClientHandler> getConnectedClients() {
        return connectedClients;
    }

    public Chain getChain() {
        return chain;
    }
}
