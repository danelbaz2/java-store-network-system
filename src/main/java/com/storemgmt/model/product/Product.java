package com.storemgmt.model.product;

import java.util.Objects;
import java.util.Set;

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
        // Pick a random product from config.json
        Set<String> productIds = JsonLoader.getKeys("products");
        String[] ids = productIds.toArray(new String[0]);
        String randomId = ids[(int) (Math.random() * ids.length)];

        String type = JsonLoader.getString("products." + randomId + ".type");
        String brand = JsonLoader.getString("products." + randomId + ".brand");
        double price = JsonLoader.getDouble("products." + randomId + ".price");

        BrandType brandType = BrandType.valueOf(brand);
        String productId = "P" + (productCounter++);

        switch (type) {
            case "Jean":
                return new Jean(productId, brandType, price);
            case "Bra":
                return new Bra(productId, brandType, price);
            case "RunningShoe":
                return new RunningShoe(productId, brandType, price);
            case "LongShirt":
                return new LongShirt(productId, brandType, price);
            case "Shorts":
                return new Shorts(productId, brandType, price);
            case "TShirt":
                return new TShirt(productId, brandType, price);
            case "Boxer":
                return new Boxer(productId, brandType, price);
            case "Sneaker":
                return new Sneaker(productId, brandType, price);
            default:
                return null;
        }
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
        return "ProductType: " + this.productType + ", Brand: " + this.getBrand() + ", Price: " + this.getPrice()
                + ", Purchased";
    }
}
