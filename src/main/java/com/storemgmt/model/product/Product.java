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

    public static Product getRandomProduct(String productId) {

        String brand = JsonLoader.getString("productCatalog." + productId + ".brand");
        double price = JsonLoader.getDouble("productCatalog." + productId + ".price");
        String type = JsonLoader.getString("productCatalog." + productId + ".type");
        BrandType brandType = BrandType.valueOf(brand);

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
