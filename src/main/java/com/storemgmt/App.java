package com.storemgmt;

import com.storemgmt.model.store.Chain;
import com.storemgmt.server.StoreServer;
import com.storemgmt.ui.ConsoleMenu;

/**
 * Application entry point.
 * Runs the console menu UI.
 */
public class App {
    public static void main(String[] args) {
        // new ConsoleMenu().run();
        StoreServer storeServer = new StoreServer(new Chain("Rami Levy"), 5000);
        storeServer.start();
    }
}
