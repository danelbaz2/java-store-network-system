package com.storemgmt.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import com.storemgmt.protocol.Request;
import com.storemgmt.protocol.Response;

/**
 * TCP Client that connects to the StoreServer.
 * 
 * This class handles the network communication:
 * - Connects to server at host:port
 * - Sends Request objects
 * - Receives Response objects
 * - Manages connection lifecycle
 */
public class BranchClient {

    private final String host;
    private final int port;
    private final String branchId;

    private Socket socket;
    private DataInputStream inputStream;
    private PrintStream outputStream;
    private boolean connected;

    public BranchClient(String host, int port, String branchId) {
        this.host = host;
        this.port = port;
        this.branchId = branchId;
        this.connected = false;
    }

    // Connect to the server
    public boolean connect() {
        try {
            System.out.println("Connecting to server at " + host + ":" + port + "...");
            socket = new Socket(host, port);

            // Create streams (output first to avoid deadlock)
            outputStream = new PrintStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());

            connected = true;
            System.out.println("Connected successfully!\n");
            return true;

        } catch (IOException e) {
            System.err.println("Failed to connect: " + e.getMessage());
            return false;
        }
    }

    // Send a request and wait for response
    public Response sendRequest(Request request) {
        if (!connected) {
            return Response.error("Not connected to server");
        }

        try {
            // Send the request
            outputStream.println(request.toString());
            outputStream.flush();

            // Wait for and return the response
            return Response.cast(inputStream.readLine());

        } catch (Exception e) {
            connected = false;
            return Response.error("Connection error: " + e.getMessage());
        }
    }

    // Disconnect from server
    public void disconnect() {
        connected = false;
        try {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
            if (socket != null)
                socket.close();
            System.out.println("Disconnected from server.");
        } catch (IOException e) {
            System.err.println("Error during disconnect: " + e.getMessage());
        }
    }

    // Getters
    public String getBranchId() {
        return branchId;
    }

    public boolean isConnected() {
        return connected;
    }
}
