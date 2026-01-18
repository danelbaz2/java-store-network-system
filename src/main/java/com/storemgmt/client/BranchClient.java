package com.storemgmt.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;

@SuppressWarnings("deprecation") // to get rid of the deprecated warnings for readLine(), etc
public class BranchClient {

    private String host;
    private int port;
    private String branchId;
    private String employeeId;
    private Socket socket;
    private DataInputStream in;
    private PrintStream out;
    private boolean connected;

    private String pendingResponse = null;
    private final Object responseLock = new Object();
    private Thread listenerThread;

    private boolean inChatMode = false;
    private String chatPartnerId = null;
    private BranchMenu menu = null;

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

            listenerThread = new Thread(() -> {
                try {
                    while (connected) {
                        String line = in.readLine();
                        if (line == null) {
                            connected = false;
                            break;
                        }
                        if (line.startsWith("CHAT_REQUEST|")) {
                            String[] parts = line.split("\\|");
                            if (parts.length >= 3 && menu != null) {
                                String fromEmpId = parts[1];
                                String fromName = parts[2];
                                menu.onChatRequest(fromEmpId, fromName);
                            }
                        } else if (line.startsWith("CHAT_MESSAGE|")) {
                            String[] parts = line.split("\\|", 3);
                            if (parts.length >= 3 && menu != null) {
                                String fromEmpId = parts[1];
                                String message = parts[2];
                                menu.onChatMessage(fromEmpId, message);
                            }
                        } else if (line.startsWith("CHAT_END|")) {
                            String[] parts = line.split("\\|");
                            if (parts.length >= 2 && menu != null) {
                                menu.onChatEnded(parts[1]);
                            }
                        } else if (line.startsWith("CHAT_AVAILABLE|")) {
                            String[] parts = line.split("\\|");
                            if (parts.length >= 2 && menu != null) {
                                menu.onAvailabilityNotification(parts[1]);
                            }
                        } else if (line.startsWith("CHAT_REQUEST_PENDING|")) {
                            String[] parts = line.split("\\|");
                            if (parts.length >= 2 && menu != null) {
                                menu.onChatRequestPending(parts[1]);
                            }

                        } else {
                            synchronized (responseLock) {
                                pendingResponse = line;
                                responseLock.notify();
                            }
                        }
                    }
                } catch (IOException e) {
                    if (connected)
                        connected = false;
                }
            });
            listenerThread.start();
            return connected;
        } catch (IOException e) {
            System.out.println("Failed to connect: " + e.getMessage());
            return false;
        }
    }

    public String[] send(String message) {
        if (!connected) {
            return new String[] { "ERROR", "Not connected" };
        }

        try {
            synchronized (responseLock) {
                pendingResponse = null;

                out.println(message);
                out.flush();

                while (pendingResponse == null && connected) {
                    responseLock.wait(30000);
                    if (pendingResponse == null) {
                        return new String[] { "ERROR", "Timeout" };
                    }
                }

                return parseResponse(pendingResponse);
            }
        } catch (InterruptedException e) {
            return new String[] { "ERROR", "Interrupted" };
        } catch (Exception e) {
            connected = false;
            return new String[] { "ERROR", e.getMessage() };
        }
    }

    private String[] parseResponse(String line) {
        if (line == null) {
            return new String[] { "ERROR", "No response" };
        }
        return line.split("\\|");
    }

    public static HashMap<String, String> getData(String[] parts) {
        HashMap<String, String> data = new HashMap<>();
        for (int i = 1; i < parts.length; i++) {
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
            if (listenerThread != null)
                listenerThread.interrupt();
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

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public boolean isInChatMode() {
        return inChatMode;
    }

    public void setInChatMode(boolean inChatMode) {
        this.inChatMode = inChatMode;
    }

    public String getChatPartnerId() {
        return chatPartnerId;
    }

    public void setChatPartnerId(String chatPartnerId) {
        this.chatPartnerId = chatPartnerId;
    }

    public void setMenu(BranchMenu menu) {
        this.menu = menu;
    }
}
