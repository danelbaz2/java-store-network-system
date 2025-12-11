import java.util.HashMap;

public abstract class Customer {
    private String fullName;
    private String id;
    private String phoneNum;
    private HashMap<Product, Integer> shoppingList = new HashMap<Product, Integer>();

    public abstract double getDiscount();
    public abstract HashMap<Product, Integer> startBuying();

    public Customer(String fullName, String id, String phoneNum)
    {
        this.fullName = fullName;
        this.id = id;
        this.phoneNum = phoneNum;
        generateShoppingList();
    }

	private void generateShoppingList() {
		for(int i = 0; i < (int)(Math.random() * 10) + 1; i++) {
			
			int amount = (int)(Math.random() * 2) + 1;
			this.shoppingList.put(Product.getRandomProduct(), amount);
		}
		
	}
	public String getFullName() {
		return fullName;
	}

	public String getId() {
		return id;
	}

	public String getPhoneNum() {
		return phoneNum;
	}
	
	@Override
	public String toString() {
		return this.fullName + "\n" + this.id + "\n" + this.phoneNum + "\n";
	}
	
	public HashMap<Product, Integer> getShoppingList() {
		return this.shoppingList;
	}
   
}
