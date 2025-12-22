package com.storemgmt.client;

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

        System.out.print("Enter your host:");
        String host = scanner.nextLine();

        System.out.print("Enter your port:");
        int port = Integer.parseInt(scanner.nextLine());

        System.out.println("========================================");
        System.out.println("     Store Branch Client - " + branchId);
        System.out.println("========================================\n");

        BranchClient client = new BranchClient(host, port, branchId);

        if (client.connect()) {
            BranchMenu menu = new BranchMenu(client);
            menu.run();
        } else {
            System.out.println("Could not connect to server. Make sure the server is running.");
        }
    }
}