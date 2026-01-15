package com.storemgmt.model.customer;

import com.storemgmt.utils.JsonLoader;

public class ReturningCustomer extends Customer {

    public ReturningCustomer(String fullName, String id, String phoneNum) {
        super(fullName, id, phoneNum);
    }

    @Override
    public double getDiscount() {
        return JsonLoader.getDouble("customers.ReturningCustomer");
    }

    @Override
    public String toString() {
        return super.toString() + this.getClass().getSimpleName();
    }

}
