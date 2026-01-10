package com.storemgmt.server;

import com.storemgmt.model.customer.Customer;
import com.storemgmt.model.store.StoreBranch;
import com.storemgmt.utils.Logger;

/**
 * Handles the checkout process for a single customer in a separate thread.
 * Simulates checkout time with a random delay, then processes the actual sale.
 */
public class CustomerHandler implements Runnable {

    private final Customer customer;
    private final StoreBranch branch;
    private final String cashierId; // The employee serving this customer

    public CustomerHandler(Customer customer, StoreBranch branch, String cashierId) {
        this.customer = customer;
        this.branch = branch;
        this.cashierId = cashierId;
    }

    @Override
    public void run() {

        try {

            // Simulate checkout time (2-5 seconds)
            Thread.sleep(2000 + (int) (Math.random() * 3000));

            // Execute the actual sale (synchronized in StoreBranch)
            double total = branch.sell(customer, cashierId);

            Logger.write("Cashier " + cashierId +
                    " completed sale for: " + customer.getFullName() +
                    " | Total: $" + String.format("%.2f", total));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // IMPORTANT: Mark the cashier as free when done
            branch.setCashierBusy(cashierId, false);
        }
    }
}
