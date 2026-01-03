package com.storemgmt.server;

import com.storemgmt.security.PasswordPolicy;
import com.storemgmt.service.AuthService;
import com.storemgmt.service.SessionManager;
import com.storemgmt.store.EmployeeStore;
import com.storemgmt.utils.ILogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {
    private final int port;
    private final AuthService authService;

    public ServerApp(int port) {
        this.port = port;

        SessionManager sessionManager = new SessionManager();

        // EmployeeStore requires an explicit path to the CSV file
        EmployeeStore employeeStore = new EmployeeStore(
            "C:\\Users\\anna.pomortzev\\Documents\\project-store-management\\store-management-system\\employees.csv"
        );

        PasswordPolicy passwordPolicy = PasswordPolicy.defaultPolicy();

        ILogger logger = new ConsoleLogger();

        this.authService = new AuthService(employeeStore, sessionManager, passwordPolicy, logger);
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New connection from " + socket.getRemoteSocketAddress());

                // ClientHandler constructor: (Socket, AuthService)
                ClientHandler handler = new ClientHandler(socket, authService);
                new Thread(handler).start();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 5050;
        new ServerApp(port).start();
    }

    // Simple console-based logger implementation
    private static class ConsoleLogger implements ILogger {
        @Override
        public void info(String message) {
            System.out.println("[INFO] " + message);
        }

        @Override
        public void error(String message) {
            System.err.println("[ERROR] " + message);
        }

        @Override
        public void error(String message, Exception ex) {
            System.err.println("[ERROR] " + message);
            if (ex != null) {
                ex.printStackTrace();
            }
        }
    }
}
