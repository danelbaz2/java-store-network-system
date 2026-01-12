package com.storemgmt.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BranchClient {

    private String host;
    private int port;
    private String branchId;
    private String employeeId;
    private Socket socket;
    private DataInputStream in;
    private PrintStream out;
    private boolean connected;
    private Runnable onChatMessage;
    private volatile boolean chatMessageReceived = false;
    private final BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();
    private volatile boolean isListening = false; 
    private Runnable onChatEnded; 


    public BranchClient(String host, int port, String branchId) {
        this.host = host;
        this.port = port;
        this.branchId = branchId;
        this.connected = false;
        this.employeeId = "";
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
        String response = "";
        if (!connected) {
            return new String[] { "ERROR", "Not connected" };
        }

        try {
            out.println(message);

            if (isListening) {
                 response = responseQueue.poll(5, TimeUnit.SECONDS);
            } else {
                 response = in.readLine();

            }
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

    public void startListening() {
        isListening = true; 

        Thread listener = new Thread(() -> {
            try {
                String line;
                while (connected && (line = in.readLine()) != null) 
                {                    
                    if (line.startsWith("CHAT|")) 
                    {
                        String msgContent = line.substring(5);
                        System.out.println("\n[CHAT] " + msgContent);        
                        chatMessageReceived = true; 
                        if (onChatMessage != null && !msgContent.startsWith("System: Chat started")) {
                            onChatMessage.run();
                        }
                    }
                    else if (line.startsWith("CHAT_ENDED|")) {
                        System.out.println("\n[Chat] " + line.substring(11));

                        if (onChatEnded != null) {
                            onChatEnded.run();
                        }
                    }
                    else{
                        responseQueue.offer(line);
                    }
                }
            } catch (IOException e) {
                if (connected) {
                System.out.println("Connection closed.");
                }
            }
            finally {
                isListening = false;
            }
        });

        listener.setDaemon(true);
        listener.start();
    }

    public void setOnChatMessage(Runnable callback) {
        this.onChatMessage = callback;
    }

    public String getBranchId() {
        return branchId;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setOnChatEnded(Runnable callback) {
        this.onChatEnded = callback;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public boolean isChatMessageReceived() {
        return chatMessageReceived;
    }

    public void clearChatMessageFlag() {
        chatMessageReceived = false;
    }

    
}
