package com.storemgmt.model.product;

public class Boxer extends Product {

    public Boxer(String productId, BrandType brand, double price) {
        super(productId, ProductType.UNDERWEAR, brand, price);
    }
}
