package com.storemgmt.model.customer;

import com.storemgmt.utils.JsonLoader;

public class NewCustomer extends Customer {

    public NewCustomer(String fullName, String id, String phoneNum) {
        super(fullName, id, phoneNum);
    }

    @Override
    public double getDiscount() {
        return JsonLoader.getDouble("customers.NewCustomer"); // no discount
    }

    @Override
    public String toString() {
        return super.toString() + this.getClass().getSimpleName();
    }

}
