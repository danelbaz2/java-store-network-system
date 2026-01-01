package com.storemgmt.model.product;

import java.util.Objects;

public abstract class Product {

    private ProductType productType;
    private BrandType brand;
    private double price;

    public Product(ProductType productType, BrandType brand, double price) {
        this.productType = productType;
        this.brand = brand;
        this.price = price;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public BrandType getBrand() {
        return brand;
    }

    public void setBrand(BrandType brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public static Product getRandomProduct() {
        Product retVal = null;
        int random = (int) (Math.random() * 4) + 1;
        BrandType randomBrand = BrandType.values()[(int) (Math.random() * BrandType.values().length)];

        switch (random) {
            case 1:
                retVal = new Jean(randomBrand);
                break;
            case 2:
                retVal = new Bra(randomBrand);
                break;
            case 3:
                retVal = new RunningShoe(randomBrand);
                break;
            case 4:
                retVal = new LongShirt( randomBrand);
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
