package com.storemgmt.model.customer;

import com.storemgmt.utils.JsonLoader;

public class VIPCustomer extends Customer {

    public VIPCustomer(String fullName, String id, String phoneNum) {
        super(fullName, id, phoneNum);
    }

    @Override
    public double getDiscount() {
        return JsonLoader.getDouble("customers.VIPCustomer"); // 50% discount
    }

    @Override
    public String toString() {
        return super.toString() + this.getClass().getSimpleName();
    }
}
