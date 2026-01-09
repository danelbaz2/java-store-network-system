package com.storemgmt.model.product;

public class RunningShoe extends Product {

    public RunningShoe(String productId, BrandType brand, double price) {
        super(productId, ProductType.SHOE, brand, price);
    }
}
