import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Branch {
	private Chain chain;
	private String name;
	private Map<Product, Integer> products;
	private LinkedList<Employee> employees;
	private Map<Product, Integer> sellRecord;

	public Branch(Chain chain, String name) {
		this.chain = chain;
		this.name = name;
		products = new HashMap<Product, Integer>();
		employees = new LinkedList<Employee>();
		sellRecord = new HashMap<Product, Integer>();
	}

	public LinkedList<Employee> getEmployees() {
		return employees;
	}

	public LinkedList<Customer> getCustomers() {
		return this.chain.getCustomers();
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
	public void sell(Product product, int amount) {
		if (!products.containsKey(product)) {
			System.out.println("Product '" + product.getClass().getName() + "' does not exist in branch " + this.name);
			return;
		}

		int current_amount = products.get(product).intValue();

		if (current_amount < amount) {
			System.out.println(
					"Product '" + product.getClass().getName() + "' does not available in branch " + this.name);
			return;
		}

		int soldProduct = sellRecord.get(product).intValue();
		sellRecord.put(product, soldProduct + amount);

		products.put(product, current_amount - amount);
	}

	public void displayProduct() {
		for (Map.Entry<Product, Integer> entry : products.entrySet()) {
			if (entry.getValue() > 0)
				System.out.println("Product: '" + entry.getClass().getName() + "' - Amount: " + entry.getValue());
		}
	}

}
