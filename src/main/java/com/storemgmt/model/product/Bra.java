package com.storemgmt.model.product;

public class Bra extends Product {

    public Bra(String productId, BrandType brand, double price) {
        super(productId, ProductType.UNDERWEAR, brand, price);
    }
}
