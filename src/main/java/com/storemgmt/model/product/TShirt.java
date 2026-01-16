package com.storemgmt.model.product;

public class TShirt extends Product {

    public TShirt(String productId, BrandType brand, double price) {
        super(productId, ProductType.SHIRT, brand, price);
    }
}
