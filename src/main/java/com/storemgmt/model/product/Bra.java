package com.storemgmt.model.product;

public class Bra extends Product {

    public Bra(String productId, BrandType brand) {
        super(productId, ProductType.UNDERWEAR, brand, 149.99);
    }
}
