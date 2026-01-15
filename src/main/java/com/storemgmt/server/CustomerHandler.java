package com.storemgmt.server;

import com.storemgmt.model.customer.Customer;
import com.storemgmt.model.store.StoreBranch;
import com.storemgmt.utils.Logger;

public class CustomerHandler implements Runnable {

    private final Customer customer;
    private final StoreBranch branch;
    private final String cashierId;

    public CustomerHandler(Customer customer, StoreBranch branch, String cashierId) {
        this.customer = customer;
        this.branch = branch;
        this.cashierId = cashierId;
    }

    @Override
    public void run() {

        try {

            Thread.sleep(2000 + (int) (Math.random() * 3000));

            double total = branch.sell(customer, cashierId);

            Logger.write("Cashier " + cashierId +
                    " completed sale for: " + customer.getFullName() +
                    " | Total: $" + String.format("%.2f", total));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            branch.setCashierBusy(cashierId, false);
        }
    }
}
