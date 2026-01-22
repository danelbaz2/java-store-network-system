package com.storemgmt.client;

import com.storemgmt.utils.JsonLoader;
import java.nio.file.Paths;
import java.util.Scanner;

public class ClientApp {

    private static Scanner scanner = new Scanner(System.in);
    private static String branchId;

    public static void main(String[] args) {

        // loading config.json file
        JsonLoader.load(Paths.get(System.getProperty("user.dir"), "config.json").toString());

        do {
            System.out.print("Enter your branchId: ");
            branchId = scanner.nextLine();
        } while (!isBranchIdValid(branchId));

        String host = JsonLoader.getString("server.host");
        int port = JsonLoader.getInt("server.port");

        System.out.println("========================================");
        System.out.println("     Store Branch Client - " + branchId);
        System.out.println("========================================");
        System.out.println();

        BranchClient client = new BranchClient(host, port, branchId);

        // connecting to server
        if (client.connect()) {
            // creating the menu for the client
            BranchMenu menu = new BranchMenu(client);

            try {
                menu.run();
            } catch (Exception e) {
                e.printStackTrace();
            }

            client.disconnect();

        } else {
            System.out.println("Could not connect to server. Check if the server is running.");
        }
    }

    private static boolean isBranchIdValid(String branchId) {
        if (branchId == null || branchId.isEmpty()) {
            System.out.println("Branch ID cannot be empty.");
            return false;
        }

        if (!JsonLoader.getKeys("branches").contains(branchId)) {
            System.out.println("Branch ID: " + branchId + " does not exist.");
            return false;
        }

        System.out.println("Branch ID: " + branchId + " is valid.");
        return true;
    }
}