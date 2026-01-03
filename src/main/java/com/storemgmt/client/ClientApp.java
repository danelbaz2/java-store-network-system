package com.storemgmt.client;

import com.storemgmt.network.Message;
import com.storemgmt.network.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 5050;

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner sc = new Scanner(System.in)) {

            Object first = in.readObject();
            if (first instanceof Message m && m.getType() == MessageType.INFO) {
                System.out.println("SERVER: " + m.get("text", String.class));
            }

            System.out.print("Employee ID: ");
            String employeeId = sc.nextLine().trim();

            System.out.print("Password: ");
            String password = sc.nextLine().trim();

            Message login = new Message(MessageType.LOGIN_REQUEST)
                    .put("employeeId", employeeId)
                    .put("password", password);

            out.writeObject(login);
            out.flush();

            Message resp = (Message) in.readObject();
            if (resp.getType() != MessageType.LOGIN_RESPONSE) {
                System.out.println("Unexpected response: " + resp.getType());
                return;
            }

            Boolean success = resp.get("success", Boolean.class);
            if (Boolean.TRUE.equals(success)) {
                System.out.println("LOGIN SUCCESS");
                System.out.println("Employee ID: " + resp.get("employeeId", String.class));
                System.out.println("Branch ID: " + resp.get("branchId", String.class));
                System.out.println("Session ID: " + resp.get("sessionId", String.class));

            } else {
                System.out.println("LOGIN FAILED: " + resp.get("error", String.class));
                return;
            }

            System.out.println("\nType 'logout' to exit.");
            while (true) {
                String cmd = sc.nextLine().trim();
                if ("logout".equalsIgnoreCase(cmd)) {
                    Message logout = new Message(MessageType.LOGOUT_REQUEST);
                    out.writeObject(logout);
                    out.flush();

                    Message bye = (Message) in.readObject();
                    System.out.println("SERVER: " + bye.get("text", String.class));
                    break;
                } else {
                    System.out.println("Unknown command. Type 'logout'.");
                }
            }
        }
    }
}