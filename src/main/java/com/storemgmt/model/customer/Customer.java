package com.storemgmt.model.customer;

import java.util.HashMap;
import java.util.Vector;

import com.storemgmt.model.product.Product;
import com.storemgmt.utils.JsonLoader;

public abstract class Customer {
    private String fullName;
    private String id;
    private String phoneNum;
    private HashMap<Product, Integer> shoppingList = new HashMap<Product, Integer>();

    public abstract double getDiscount();

    public Customer(String fullName, String id, String phoneNum) {
        this.fullName = fullName;
        this.id = id;
        this.phoneNum = phoneNum;
        generateShoppingList();
    }

    private void generateShoppingList() {
        Vector<String> productIds = new Vector<>(JsonLoader.getKeys("productCatalog"));
        int range = (int) (Math.random() * productIds.size()) + 1;

        for (int i = 0; i < range; i++) {
            int amount = (int) (Math.random() * 5) + 1;
            String productId = productIds.get((int) (Math.random() * productIds.size()));
            this.shoppingList.put(Product.getRandomProduct(productId), amount);
            productIds.removeElement(productId);
        }
    }

    public String getFullName() {
        return fullName;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Full Name: " + this.fullName + ", ID: " + this.id + ", Phone Number: " + this.phoneNum
                + ", Customer Type: ";
    }

    public HashMap<Product, Integer> getShoppingList() {
        return this.shoppingList;
    }
}
