package com.storemgmt.model.product;

public class LongShirt extends Product {

    public LongShirt(String productId, BrandType brand) {
        super(productId, ProductType.SHIRT, brand, 119.99);
    }
}
