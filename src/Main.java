
public class Main {


	public static void main(String[] args) {
		
		Branch b = new Branch("Yavne");
		Customer c = new VIPCustomer("Shahaf", "100000005", "0500000000");
		
		System.out.println(c.getShoppingList());
		
		b.sell(c);
		b.displayProduct();
		
		
		
	}
}
