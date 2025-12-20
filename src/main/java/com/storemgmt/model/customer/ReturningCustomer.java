package com.storemgmt.model.customer;

import java.util.HashMap;
import com.storemgmt.model.product.Product;

public class ReturningCustomer extends Customer {

    public ReturningCustomer(String fullName, String id, String phoneNum) {
        super(fullName, id, phoneNum);
    }

    @Override
    public double getDiscount() {
        return 0.8; // 20% discount
    }

    @Override
    public String toString() {
        return super.toString() + this.getClass().getSimpleName();
    }

    @Override
    public HashMap<Product, Integer> startBuying() {
        return null;
    }
}
