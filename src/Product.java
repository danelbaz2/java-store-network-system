
public abstract class Product {
	
	private ProductType productType;
	private String brand;
	private double price;
	
	public Product(ProductType productType, String brand, double price) {
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

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
