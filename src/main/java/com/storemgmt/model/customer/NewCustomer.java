package com.storemgmt.model.customer;

import java.util.HashMap;
import com.storemgmt.model.product.Product;
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

    @Override
    public HashMap<Product, Integer> startBuying() {
        return null;
    }
}
