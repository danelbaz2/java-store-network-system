package com.storemgmt.model.product;

public class Shorts extends Product {

    public Shorts(String productId, BrandType brand, double price) {
        super(productId, ProductType.PANT, brand, price);
    }
}
