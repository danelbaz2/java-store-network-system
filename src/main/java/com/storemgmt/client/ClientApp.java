package com.storemgmt.client;

import com.storemgmt.utils.JsonLoader;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Entry point for the branch client application.
 * 
 * Usage:
 * java ClientApp <branchId> [host] [port]
 * 
 * Examples:
 * java ClientApp BR01 # Connect to localhost:5000
 * java ClientApp BR02 localhost 5000 # Explicit host and port
 * java ClientApp BR01 192.168.1.100 5000 # Remote server
 */
public class ClientApp {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.print("Enter your branchId:");
        String branchId = scanner.nextLine();

        JsonLoader.load(Paths.get(System.getProperty("user.dir"), "config.json").toString());
        String host = JsonLoader.getString("server.host");
        int port = JsonLoader.getInt("server.port");

        System.out.println("========================================");
        System.out.println("     Store Branch Client - " + branchId);
        System.out.println("========================================");
        System.out.println();

        BranchClient client = new BranchClient(host, port, branchId);

        if (client.connect()) {
            BranchMenu menu = new BranchMenu(client);

            try {
                menu.run();
            } catch (Exception e) {
                e.printStackTrace();
            }

            client.disconnect(); // Close the connection

        } else {
            System.out.println("Could not connect to server. Check if the server is running.");
        }
    }
}