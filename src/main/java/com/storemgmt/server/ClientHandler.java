package com.storemgmt.server;

import com.storemgmt.model.UserSession;
import com.storemgmt.network.Message;
import com.storemgmt.network.MessageType;
import com.storemgmt.service.AuthService;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final AuthService authService;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private UserSession currentSession; // null אם לא מחובר

    public ClientHandler(Socket socket, AuthService authService) {
        this.socket = socket;
        this.authService = authService;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            send(new Message(MessageType.INFO).put("text", "Connected. Please login."));

            while (!socket.isClosed()) {
                Object obj = in.readObject();
                if (!(obj instanceof Message msg)) {
                    send(new Message(MessageType.ERROR).put("text", "Invalid message format"));
                    continue;
                }

                switch (msg.getType()) {
                    case LOGIN_REQUEST -> handleLogin(msg);
                    case LOGOUT_REQUEST -> handleLogout(msg);
                    default -> send(new Message(MessageType.ERROR).put("text", "Unsupported message type"));
                }
            }

        } catch (EOFException eof) {
            // client disconnected
        } catch (Exception e) {
            System.out.println("Client error: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void handleLogin(Message msg) throws IOException {
        if (currentSession != null) {
            send(new Message(MessageType.LOGIN_RESPONSE)
                    .put("success", false)
                    .put("error", "Already logged in"));
            return;
        }

        String employeeId = msg.get("employeeId", String.class);
        String password = msg.get("password", String.class);

        if (employeeId == null || password == null) {
            send(new Message(MessageType.LOGIN_RESPONSE)
                    .put("success", false)
                    .put("error", "Missing employeeId/password"));
            return;
        }

        try {
            currentSession = authService.login(employeeId, password);

            send(new Message(MessageType.LOGIN_RESPONSE)
                    .put("success", true)
                    .put("sessionId", currentSession.getSessionId())
                    .put("employeeId", currentSession.getEmployeeId())
                    .put("branchId", currentSession.getBranchId()));

        } catch (Exception ex) {
            send(new Message(MessageType.LOGIN_RESPONSE)
                    .put("success", false)
                    .put("error", ex.getMessage()));
        }
    }

    private void handleLogout(Message msg) throws IOException {
        String sessionId = msg.get("sessionId", String.class);

        try {
            if (sessionId != null) {
                authService.logout(sessionId);
            } else if (currentSession != null) {
                authService.logout(currentSession.getSessionId());
            }

            currentSession = null;

            send(new Message(MessageType.INFO).put("text", "Logged out"));
            socket.close();

        } catch (Exception ex) {
            send(new Message(MessageType.ERROR).put("text", ex.getMessage()));
        }
    }

    private void send(Message msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    private void cleanup() {
        try {
            if (currentSession != null) {
                authService.logout(currentSession.getSessionId());
                currentSession = null;
            }
        } catch (Exception ignored) { }

        try { if (in != null) in.close(); } catch (Exception ignored) { }
        try { if (out != null) out.close(); } catch (Exception ignored) { }
        try { if (socket != null && !socket.isClosed()) socket.close(); } catch (Exception ignored) { }

        System.out.println("Connection closed: " + socket);
    }
}
