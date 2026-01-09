package com.storemgmt.model.product;

import java.util.Objects;

import com.storemgmt.utils.JsonLoader;

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
        int random = (int) (Math.random() * 8) + 1;
        BrandType randomBrand = BrandType.values()[(int) (Math.random() * BrandType.values().length)];
        String productId = "P" + (productCounter++);

        switch (random) {
            case 1:
                retVal = new Jean(productId, randomBrand, getPriceByType("Jean"));
                break;
            case 2:
                retVal = new Bra(productId, randomBrand, getPriceByType("Bra"));
                break;
            case 3:
                retVal = new RunningShoe(productId, randomBrand, getPriceByType("RunningShoe"));
                break;
            case 4:
                retVal = new LongShirt(productId, randomBrand, getPriceByType("LongShirt"));
                break;
            case 5:
                retVal = new Shorts(productId, randomBrand, getPriceByType("Shorts"));
                break;
            case 6:
                retVal = new TShirt(productId, randomBrand, getPriceByType("TShirt"));
                break;
            case 7:
                retVal = new Boxer(productId, randomBrand, getPriceByType("Boxer"));
                break;
            case 8:
                retVal = new Sneaker(productId, randomBrand, getPriceByType("Sneaker"));
                break;
        }

        return retVal;
    }

    private static double getPriceByType(String productType) {

        for (String productId : JsonLoader.getKeys("products")) {
            String type = JsonLoader.getString("products." + productId + ".type");
            if (type.equals(productType)) {
                return JsonLoader.getDouble("products." + productId + ".price");
            }
        }

        return 0;
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
        return this.getClass().getSimpleName() + " " + this.getBrand();
    }
}
