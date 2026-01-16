package com.storemgmt.model.product;

public class Jean extends Product {

    public Jean(String productId, BrandType brand, double price) {
        super(productId, ProductType.PANT, brand, price);
    }
}
