package com.storemgmt.model.product;

public class Sneaker extends Product {

    public Sneaker(String productId, BrandType brand, double price) {
        super(productId, ProductType.SHOE, brand, price);
    }
}
