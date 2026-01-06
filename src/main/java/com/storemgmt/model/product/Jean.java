package com.storemgmt.model.product;

public class Jean extends Product {

    public Jean(String productId, BrandType brand) {
        super(productId, ProductType.PANT, brand, 199.99);
    }
}
