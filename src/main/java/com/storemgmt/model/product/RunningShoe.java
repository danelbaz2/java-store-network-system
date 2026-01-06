package com.storemgmt.model.product;

public class RunningShoe extends Product {

    public RunningShoe(String productId, BrandType brand) {
        super(productId, ProductType.SHOE, brand, 499.99);
    }
}
