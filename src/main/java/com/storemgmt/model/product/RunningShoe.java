package com.storemgmt.model.product;

public class RunningShoe extends Product {

    public RunningShoe(ProductType productType, BrandType brand) {
        super(ProductType.SHOE, brand, 499.99);
    }
}
