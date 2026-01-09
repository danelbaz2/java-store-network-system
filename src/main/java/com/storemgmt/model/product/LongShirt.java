package com.storemgmt.model.product;

public class LongShirt extends Product {

    public LongShirt(String productId, BrandType brand, double price) {
        super(productId, ProductType.SHIRT, brand, price);
    }
}
