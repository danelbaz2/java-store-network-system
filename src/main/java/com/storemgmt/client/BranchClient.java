package com.storemgmt.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;

public class BranchClient {

    private String host;
    private int port;
    private String branchId;

    private Socket socket;
    private DataInputStream in;
    private PrintStream out;
    private boolean connected;

    public BranchClient(String host, int port, String branchId) {
        this.host = host;
        this.port = port;
        this.branchId = branchId;
        this.connected = false;
    }

    public boolean connect() {
        try {
            System.out.println("Connecting to " + host + ":" + port + "...");
            socket = new Socket(host, port);
            out = new PrintStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            connected = true;
            System.out.println("Connected!\n");
            return true;
        } catch (IOException e) {
            System.out.println("Failed to connect: " + e.getMessage());
            return false;
        }
    }

    // Format: ACTION|branchId|k1:v1|k2:v2|...

    public String[] send(String message) {
        if (!connected) {
            return new String[] { "ERROR", "Not connected" };
        }

        try {
            out.println(message);
            out.flush();

            String response = in.readLine();
            return parseResponse(response);

        } catch (Exception e) {
            connected = false;
            return new String[] { "ERROR", e.getMessage() };
        }
    }

    // Parse response: OK|message|key1:value1|key2:value2
    private String[] parseResponse(String line) {
        if (line == null) {
            return new String[] { "ERROR", "No response" };
        }
        return line.split("\\|");
    }

    // Helper to get data from parsed response
    public static HashMap<String, String> getData(String[] parts) {
        HashMap<String, String> data = new HashMap<>();
        for (int i = 2; i < parts.length; i++) {
            String[] kv = parts[i].split(":", 2);
            if (kv.length == 2) {
                data.put(kv[0], kv[1]);
            }
        }
        return data;
    }

    public void disconnect() {
        connected = false;
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
            System.out.println("Disconnected.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public String getBranchId() {
        return branchId;
    }

    public boolean isConnected() {
        return connected;
    }
}
