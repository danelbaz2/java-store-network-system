package com.storemgmt.model.product;

import java.util.Objects;

public abstract class Product {

    private final String productId;
    private final ProductType productType;
    private final BrandType brand;
    private double price;

    public Product(String productId, ProductType productType, BrandType brand, double price) {
        this.productId = productId;
        this.productType = productType;
        this.brand = brand;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public ProductType getProductType() {
        return productType;
    }

    public BrandType getBrand() {
        return brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    private static int productCounter = 1000;

    public static Product getRandomProduct() {
        Product retVal = null;
        int random = (int) (Math.random() * 4) + 1;
        BrandType randomBrand = BrandType.values()[(int) (Math.random() * BrandType.values().length)];
        String productId = "P" + (productCounter++);

        switch (random) {
            case 1:
                retVal = new Jean(productId, randomBrand);
                break;
            case 2:
                retVal = new Bra(productId, randomBrand);
                break;
            case 3:
                retVal = new RunningShoe(productId, randomBrand);
                break;
            case 4:
                retVal = new LongShirt(productId, randomBrand);
                break;
        }

        return retVal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Product product = (Product) o;
        return productType == product.productType && brand == product.brand;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productType, brand);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + this.getBrand() + " : " + this.getPrice() + "$";
    }
}
