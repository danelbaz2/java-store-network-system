import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Branch {
	private String name;
	private Map<Product, Integer> products;
	private LinkedList<Employee> employees;
	private Map<Product, Integer> sellRecord;

	public Branch(String name) {
		this.name = name;
		products = new HashMap<Product, Integer>();
		employees = new LinkedList<Employee>();
		sellRecord = new HashMap<Product, Integer>();
		addProduct();
	}

	private void addProduct() {
		this.products.put(new Jean(ProductType.PANT, BrandType.ADIDAS), 50);
		this.products.put(new Jean(ProductType.PANT, BrandType.BERSHKA), 50);
		this.products.put(new Jean(ProductType.PANT, BrandType.LEVIS), 50);
		this.products.put(new Jean(ProductType.PANT, BrandType.NIKE), 50);

		this.products.put(new LongShirt(ProductType.SHIRT, BrandType.BERSHKA), 50);
		this.products.put(new LongShirt(ProductType.SHIRT, BrandType.LEVIS), 50);
		this.products.put(new LongShirt(ProductType.SHIRT, BrandType.POLO), 50);

		this.products.put(new Bra(ProductType.UNDERWEAR, BrandType.NIKE), 50);
		this.products.put(new Bra(ProductType.UNDERWEAR, BrandType.ADIDAS), 50);
		this.products.put(new Bra(ProductType.UNDERWEAR, BrandType.BERSHKA), 50);

		this.products.put(new RunningShoe(ProductType.SHOE, BrandType.NIKE), 50);
		this.products.put(new RunningShoe(ProductType.SHOE, BrandType.ADIDAS), 50);
	}

	public LinkedList<Employee> getEmployees() {
		return employees;
	}

	public void addEmployee(Employee employee) {
		employee.setBranch(this.name);

		employees.add(employee);
	}

	public void removeEmployee(Employee employee) {
		employees.remove(employee);
	}

	// sell method, check if the product exists/availables, else return.
	// If available, update sellRecord and remove from inventory.
	public void sell(Customer customer) {

		double sum = 0;
		Map<Product, Integer> shoppingList = customer.getShoppingList(); // get the list of items that the customer want
																			// to buy
		for (Map.Entry<Product, Integer> product : shoppingList.entrySet()) { // iterate

			if (!products.containsKey(product.getKey())) { // if the product does not exist in the branch, continue to
															// the next product to buy
				System.out.println("Product '" + product.getKey().getClass().getName() + " "
						+ product.getKey().getBrand() + "' does not exist in branch " + this.name);
				continue;
			}

			int currentAmount = products.get(product.getKey()).intValue(); // if exist, get the amount in the inventory
			int amountToBuy = product.getValue();

			if (currentAmount < amountToBuy) { // if there is not enough item in the inventory, cannot buy the item
				System.out.println("Product '" + product.getKey().getClass().getName()
						+ "' does not available in branch " + this.name);
				continue;
			}
			
			if(!sellRecord.containsKey(product.getKey())) {
				sellRecord.put(product.getKey(), 0);
			}
			
			int soldProduct = sellRecord.get(product.getKey()).intValue();
			sellRecord.put(product.getKey(), soldProduct + amountToBuy);

			products.put(product.getKey(), currentAmount - amountToBuy);

			sum += product.getKey().getPrice() * amountToBuy; // calculate the sum to pay at the end
		}

		System.out.println("Customer " + customer.getFullName() + " gets the " + customer.getClass().getName()
				+ " reduction, and then pay instead of\n" + sum + "$\n" + sum * customer.getDiscount() + "$.");

	}

	public void displayProduct() {
		for (Map.Entry<Product, Integer> entry : products.entrySet()) {
			if (entry.getValue() > 0)
				System.out.println("Product: '" + entry.getClass().getName() + "' - Amount: " + entry.getValue());
		}
	}

}
